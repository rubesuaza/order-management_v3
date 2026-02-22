"""Tests for the Money Value Object."""

from decimal import Decimal

import pytest

from order_management.domain.exceptions import CurrencyMismatchError
from order_management.domain.models.value_objects import Money


def test_money_creation_with_default_currency() -> None:
    """Money should be created with USD by default."""
    money = Money(amount=Decimal("100.00"))
    assert money.amount == Decimal("100.00")
    assert money.currency == "USD"


def test_money_creation_with_explicit_currency() -> None:
    """Money should accept explicit currency."""
    money = Money(amount=Decimal("50.00"), currency="EUR")
    assert money.currency == "EUR"


def test_money_is_immutable() -> None:
    """Money should be immutable (frozen)."""
    money = Money(amount=Decimal("10.00"))
    with pytest.raises(AttributeError):
        money.amount = Decimal("20.00")  # type: ignore[misc]


def test_money_add_same_currency() -> None:
    """Money should add amounts of the same currency."""
    a = Money(amount=Decimal("10.00"))
    b = Money(amount=Decimal("5.50"))
    result = a + b
    assert result.amount == Decimal("15.50")
    assert result.currency == "USD"


def test_money_add_different_currencies_raises() -> None:
    """Money should raise CurrencyMismatchError when adding different currencies."""
    a = Money(amount=Decimal("10.00"), currency="USD")
    b = Money(amount=Decimal("5.00"), currency="EUR")
    with pytest.raises(CurrencyMismatchError):
        a + b


def test_money_subtract_same_currency() -> None:
    """Money should subtract amounts of the same currency."""
    a = Money(amount=Decimal("10.00"))
    b = Money(amount=Decimal("3.00"))
    result = a - b
    assert result.amount == Decimal("7.00")


def test_money_subtract_different_currencies_raises() -> None:
    """Money should raise CurrencyMismatchError when subtracting different currencies."""
    a = Money(amount=Decimal("10.00"), currency="USD")
    b = Money(amount=Decimal("5.00"), currency="GBP")
    with pytest.raises(CurrencyMismatchError):
        a - b


def test_money_multiply_by_int() -> None:
    """Money should multiply by an integer (quantity)."""
    money = Money(amount=Decimal("2.50"))
    result = money * 3
    assert result.amount == Decimal("7.50")
    assert result.currency == "USD"


def test_money_multiply_by_decimal() -> None:
    """Money should multiply by Decimal."""
    money = Money(amount=Decimal("2.50"))
    result = money * Decimal("2.5")
    assert result.amount == Decimal("6.25")


def test_money_zero() -> None:
    """Money.zero() should create zero amount in USD."""
    zero = Money.zero()
    assert zero.amount == Decimal("0")
    assert zero.currency == "USD"
