"""Mappers entre entidades de dominio y modelos de persistencia."""

from dataclasses import replace
from datetime import datetime
from decimal import Decimal
from uuid import UUID

from order_management.domain.models.enums import OrderStatus
from order_management.domain.models.order import Order
from order_management.domain.models.order_item import OrderItem
from order_management.domain.models.value_objects import Money

from .models import OrderItemModel, OrderModel


def order_model_to_domain(model: OrderModel) -> Order:
    """Convierte OrderModel a entidad de dominio Order."""
    items = [
        OrderItem(
            product_id=UUID(str(item.product_id)),
            quantity=item.quantity,
            unit_price=Money(amount=item.unit_price, currency=model.currency),
        )
        for item in model.items
    ]
    order = Order(customer_id=UUID(str(model.customer_id)), items=items)
    return replace(
        order,
        id=UUID(str(model.id)),
        status=OrderStatus(model.status),
        created_at=model.created_at,
    )


def order_domain_to_model(order: Order) -> OrderModel:
    """Convierte entidad Order a OrderModel."""
    currency = order.total_amount.currency if order.items else "USD"
    model = OrderModel(
        id=str(order.id),
        customer_id=str(order.customer_id),
        status=order.status.value,
        total_amount=order.total_amount.amount,
        currency=currency,
        created_at=order.created_at,
    )
    for item in order.items:
        item_model = OrderItemModel(
            order_id=str(order.id),
            product_id=str(item.product_id),
            quantity=item.quantity,
            unit_price=item.unit_price.amount,
        )
        model.items.append(item_model)
    return model
