"""Tests del Value Object Money."""

from decimal import Decimal

import pytest

from order_management.domain.exceptions import CurrencyMismatchError
from order_management.domain.models.value_objects import Money


def test_money_creation_with_default_currency() -> None:
    """Money debe crearse con USD por defecto."""
    money = Money(amount=Decimal("100.00"))
    assert money.amount == Decimal("100.00")
    assert money.currency == "USD"


def test_money_creation_with_explicit_currency() -> None:
    """Money debe aceptar moneda explícita."""
    money = Money(amount=Decimal("50.00"), currency="EUR")
    assert money.currency == "EUR"


def test_money_is_immutable() -> None:
    """Money debe ser inmutable (frozen)."""
    money = Money(amount=Decimal("10.00"))
    with pytest.raises(AttributeError):
        money.amount = Decimal("20.00")  # type: ignore[misc]


def test_money_add_same_currency() -> None:
    """Money debe sumar cantidades de la misma moneda."""
    a = Money(amount=Decimal("10.00"))
    b = Money(amount=Decimal("5.50"))
    result = a + b
    assert result.amount == Decimal("15.50")
    assert result.currency == "USD"


def test_money_add_different_currencies_raises() -> None:
    """Money debe lanzar CurrencyMismatchError al sumar monedas distintas."""
    a = Money(amount=Decimal("10.00"), currency="USD")
    b = Money(amount=Decimal("5.00"), currency="EUR")
    with pytest.raises(CurrencyMismatchError):
        a + b


def test_money_subtract_same_currency() -> None:
    """Money debe restar cantidades de la misma moneda."""
    a = Money(amount=Decimal("10.00"))
    b = Money(amount=Decimal("3.00"))
    result = a - b
    assert result.amount == Decimal("7.00")


def test_money_subtract_different_currencies_raises() -> None:
    """Money debe lanzar CurrencyMismatchError al restar monedas distintas."""
    a = Money(amount=Decimal("10.00"), currency="USD")
    b = Money(amount=Decimal("5.00"), currency="GBP")
    with pytest.raises(CurrencyMismatchError):
        a - b


def test_money_multiply_by_int() -> None:
    """Money debe multiplicarse por un entero (cantidad)."""
    money = Money(amount=Decimal("2.50"))
    result = money * 3
    assert result.amount == Decimal("7.50")
    assert result.currency == "USD"


def test_money_multiply_by_decimal() -> None:
    """Money debe multiplicarse por Decimal."""
    money = Money(amount=Decimal("2.50"))
    result = money * Decimal("2.5")
    assert result.amount == Decimal("6.25")


def test_money_zero() -> None:
    """Money.zero() debe crear cantidad cero en USD."""
    zero = Money.zero()
    assert zero.amount == Decimal("0")
    assert zero.currency == "USD"
