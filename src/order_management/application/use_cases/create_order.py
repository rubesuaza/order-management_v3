"""Use case: Create order."""

from order_management.application.ports.in_.create_order import (
    CreateOrderInput,
    CreateOrderPort,
    CreateOrderResult,
)
from order_management.application.ports.out import OrderRepository
from order_management.domain.models.order import Order
from order_management.domain.models.order_item import OrderItem
from order_management.domain.models.value_objects import Money


class CreateOrderUseCase(CreateOrderPort):
    """Implementation of the create order use case."""

    def __init__(self, order_repository: OrderRepository) -> None:
        self._order_repository = order_repository

    async def execute(self, input_data: CreateOrderInput) -> CreateOrderResult:
        """Creates an order with the provided items."""
        items = [
            OrderItem(
                product_id=item.product_id,
                quantity=item.quantity,
                unit_price=Money(amount=item.unit_price, currency=item.currency),
            )
            for item in input_data.items
        ]
        order = Order(customer_id=input_data.customer_id, items=items)
        saved_order = await self._order_repository.save(order)
        return CreateOrderResult(order=saved_order)
