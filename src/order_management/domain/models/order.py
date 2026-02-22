"""Agregado Order - Raíz del contexto de Gestión de Pedidos."""

from dataclasses import dataclass, field
from datetime import datetime, timezone
from decimal import Decimal
from uuid import UUID, uuid4

from order_management.domain.exceptions import InvalidItemError, InvalidOrderStateError
from order_management.domain.models.enums import OrderStatus
from order_management.domain.models.order_item import OrderItem
from order_management.domain.models.value_objects import Money

MINIMUM_ORDER_AMOUNT = Decimal("10.00")


@dataclass
class Order:
    """Raíz del agregado de pedidos."""

    customer_id: UUID
    items: list[OrderItem]
    id: UUID = field(default_factory=uuid4)
    status: OrderStatus = field(default=OrderStatus.PENDING, init=False)
    created_at: datetime = field(default_factory=lambda: datetime.now(timezone.utc), init=False)

    def __post_init__(self) -> None:
        if not self.items:
            raise InvalidItemError("Un pedido debe tener al menos un ítem")

    @property
    def total_amount(self) -> Money:
        """Calcula el total como suma de (unit_price * quantity) de todos los ítems."""
        total = Money.zero()
        for item in self.items:
            total = total + item.line_total
        return total

    def mark_paid(self) -> None:
        """Transición a PAID. Requiere total >= 10.00 USD."""
        if self.status != OrderStatus.PENDING:
            raise InvalidOrderStateError(
                f"Solo un pedido PENDING puede pagarse. Estado actual: {self.status}"
            )
        if self.total_amount.amount < MINIMUM_ORDER_AMOUNT:
            raise InvalidOrderStateError(
                f"El monto mínimo para procesar es 10.00 USD. Total actual: {self.total_amount.amount}"
            )
        object.__setattr__(self, "status", OrderStatus.PAID)

    def cancel(self) -> None:
        """Solo PENDING o PAID pueden cancelarse."""
        if self.status not in (OrderStatus.PENDING, OrderStatus.PAID):
            raise InvalidOrderStateError(
                f"Solo pedidos PENDING o PAID pueden cancelarse. Estado actual: {self.status}"
            )
        object.__setattr__(self, "status", OrderStatus.CANCELLED)

    def mark_shipped(self) -> None:
        """Solo PAID puede enviarse."""
        if self.status != OrderStatus.PAID:
            raise InvalidOrderStateError(
                f"Solo un pedido PAID puede enviarse. Estado actual: {self.status}"
            )
        object.__setattr__(self, "status", OrderStatus.SHIPPED)

    def mark_delivered(self) -> None:
        """Solo SHIPPED puede marcarse como entregado."""
        if self.status != OrderStatus.SHIPPED:
            raise InvalidOrderStateError(
                f"Solo un pedido SHIPPED puede marcarse como entregado. Estado actual: {self.status}"
            )
        object.__setattr__(self, "status", OrderStatus.DELIVERED)
