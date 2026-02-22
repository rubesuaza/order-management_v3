"""Entidad OrderItem del dominio."""

from dataclasses import dataclass
from decimal import Decimal
from uuid import UUID

from order_management.domain.exceptions import InvalidItemError
from order_management.domain.models.value_objects import Money


@dataclass
class OrderItem:
    """Ítem de un pedido."""

    product_id: UUID
    quantity: int
    unit_price: Money

    def __post_init__(self) -> None:
        if self.quantity <= 0:
            raise InvalidItemError("La cantidad debe ser estrictamente mayor que cero")
        if self.unit_price.amount < 0:
            raise InvalidItemError("El precio unitario no puede ser negativo")

    @property
    def line_total(self) -> Money:
        """Total de la línea: unit_price * quantity."""
        return self.unit_price * self.quantity
