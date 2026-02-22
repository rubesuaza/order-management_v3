"""Tests de excepciones de dominio."""

import pytest

from order_management.domain.exceptions import (
    CurrencyMismatchError,
    DomainError,
    InvalidItemError,
    InvalidOrderStateError,
)


def test_domain_error_is_base_exception() -> None:
    """DomainError debe ser la clase base de todas las excepciones de dominio."""
    error = DomainError("Mensaje de prueba")
    assert isinstance(error, Exception)
    assert str(error) == "Mensaje de prueba"


def test_invalid_order_state_error_extends_domain_error() -> None:
    """InvalidOrderStateError debe heredar de DomainError."""
    error = InvalidOrderStateError("Estado inválido")
    assert isinstance(error, DomainError)


def test_invalid_item_error_extends_domain_error() -> None:
    """InvalidItemError debe heredar de DomainError."""
    error = InvalidItemError("Ítem inválido")
    assert isinstance(error, DomainError)


def test_currency_mismatch_error_extends_domain_error() -> None:
    """CurrencyMismatchError debe heredar de DomainError."""
    error = CurrencyMismatchError("Monedas diferentes")
    assert isinstance(error, DomainError)
