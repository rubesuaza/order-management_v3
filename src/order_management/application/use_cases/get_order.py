"""Caso de uso: Obtener detalle de pedido."""

from uuid import UUID

from order_management.application.ports.in_.get_order import GetOrderPort
from order_management.application.ports.out import OrderRepository
from order_management.domain.exceptions import OrderNotFoundError
from order_management.domain.models.order import Order


class GetOrderUseCase:
    """Implementación del caso de uso de obtener pedido."""

    def __init__(self, order_repository: OrderRepository) -> None:
        self._order_repository = order_repository

    async def execute(self, order_id: UUID) -> Order:
        """Obtiene un pedido por ID."""
        order = await self._order_repository.get_by_id(order_id)
        if order is None:
            raise OrderNotFoundError(f"Pedido no encontrado: {order_id}")
        return order
