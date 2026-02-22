"""Value Objects del dominio - Inmutables."""

from dataclasses import dataclass
from decimal import Decimal
from typing import Union

from order_management.domain.exceptions import CurrencyMismatchError


@dataclass(frozen=True)
class Money:
    """Objeto de valor para cantidades monetarias."""

    amount: Decimal
    currency: str = "USD"

    def __add__(self, other: "Money") -> "Money":
        if self.currency != other.currency:
            raise CurrencyMismatchError(
                f"No se pueden sumar monedas diferentes: {self.currency} y {other.currency}"
            )
        return Money(amount=self.amount + other.amount, currency=self.currency)

    def __sub__(self, other: "Money") -> "Money":
        if self.currency != other.currency:
            raise CurrencyMismatchError(
                f"No se pueden restar monedas diferentes: {self.currency} y {other.currency}"
            )
        return Money(amount=self.amount - other.amount, currency=self.currency)

    def __mul__(self, other: Union[int, Decimal]) -> "Money":
        if isinstance(other, (int, Decimal)):
            return Money(amount=self.amount * Decimal(str(other)), currency=self.currency)
        return NotImplemented

    @classmethod
    def zero(cls, currency: str = "USD") -> "Money":
        """Crea un Money con cantidad cero."""
        return cls(amount=Decimal("0"), currency=currency)


@dataclass(frozen=True)
class Address:
    """Objeto de valor para direcciones."""

    street: str
    city: str
    zip_code: str
    country: str
