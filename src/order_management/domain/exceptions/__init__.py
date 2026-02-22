"""Excepciones específicas del dominio."""

from order_management.domain.exceptions.base import (
    CurrencyMismatchError,
    DomainError,
    InvalidItemError,
    InvalidOrderStateError,
)

__all__ = [
    "DomainError",
    "InvalidOrderStateError",
    "InvalidItemError",
    "CurrencyMismatchError",
]
