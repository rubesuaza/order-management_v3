"""Use case: Get order details."""

from uuid import UUID

from order_management.application.ports.in_.get_order import GetOrderPort
from order_management.application.ports.out import OrderRepository
from order_management.domain.exceptions import OrderNotFoundError
from order_management.domain.models.order import Order


class GetOrderUseCase(GetOrderPort):
    """Implementation of the get order use case."""

    def __init__(self, order_repository: OrderRepository) -> None:
        self._order_repository = order_repository

    async def execute(self, order_id: UUID) -> Order:
        """Retrieves an order by ID. Raises OrderNotFoundError if it does not exist."""
        order = await self._order_repository.get_by_id(order_id)
        if order is None:
            raise OrderNotFoundError(f"Pedido no encontrado: {order_id}")
        return order
