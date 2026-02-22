"""Caso de uso: Pagar pedido."""

from uuid import UUID

from order_management.application.ports.in_.pay_order import PayOrderPort
from order_management.application.ports.out import OrderRepository
from order_management.domain.exceptions import OrderNotFoundError
from order_management.domain.models.order import Order


class PayOrderUseCase:
    """Implementación del caso de uso de pagar pedido."""

    def __init__(self, order_repository: OrderRepository) -> None:
        self._order_repository = order_repository

    async def execute(self, order_id: UUID) -> Order:
        """Procesa el pago del pedido (simulado)."""
        order = await self._order_repository.get_by_id(order_id)
        if order is None:
            raise OrderNotFoundError(f"Pedido no encontrado: {order_id}")
        order.mark_paid()
        return await self._order_repository.save(order)
