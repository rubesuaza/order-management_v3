"""Caso de uso: Crear pedido."""

from decimal import Decimal

from order_management.application.ports.in_.create_order import (
    CreateOrderInput,
    CreateOrderResult,
)
from order_management.application.ports.out import OrderRepository
from order_management.domain.models.order import Order
from order_management.domain.models.order_item import OrderItem
from order_management.domain.models.value_objects import Money


class CreateOrderUseCase:
    """Implementación del caso de uso de crear pedido."""

    def __init__(self, order_repository: OrderRepository) -> None:
        self._order_repository = order_repository

    async def execute(self, input_data: CreateOrderInput) -> CreateOrderResult:
        """Crea un pedido con los ítems proporcionados."""
        items = [
            OrderItem(
                product_id=item["product_id"],
                quantity=item["quantity"],
                unit_price=Money(
                    amount=Decimal(str(item["unit_price"])),
                    currency=item.get("currency", "USD"),
                ),
            )
            for item in input_data.items
        ]
        order = Order(customer_id=input_data.customer_id, items=items)
        saved_order = await self._order_repository.save(order)
        return CreateOrderResult(order=saved_order)
