"""Tests del agregado Order."""

from decimal import Decimal
from datetime import datetime
from uuid import uuid4

import pytest

from order_management.domain.exceptions import InvalidItemError, InvalidOrderStateError
from order_management.domain.models import Money, Order, OrderItem, OrderStatus


def test_order_creation_requires_at_least_one_item() -> None:
    """Order debe tener al menos un OrderItem para ser creado."""
    customer_id = uuid4()
    items = [
        OrderItem(product_id=uuid4(), quantity=1, unit_price=Money(Decimal("15.00"))),
    ]
    order = Order(customer_id=customer_id, items=items)
    assert order.status == OrderStatus.PENDING
    assert len(order.items) == 1
    assert order.total_amount.amount == Decimal("15.00")


def test_order_rejects_empty_items() -> None:
    """Order debe rechazar lista vacía de ítems."""
    customer_id = uuid4()
    with pytest.raises(InvalidItemError):
        Order(customer_id=customer_id, items=[])


def test_order_total_calculation() -> None:
    """Order total debe ser suma de (unit_price * quantity) de todos los ítems."""
    items = [
        OrderItem(product_id=uuid4(), quantity=2, unit_price=Money(Decimal("10.00"))),
        OrderItem(product_id=uuid4(), quantity=1, unit_price=Money(Decimal("5.50"))),
    ]
    order = Order(customer_id=uuid4(), items=items)
    # 20.00 + 5.50 = 25.50
    assert order.total_amount.amount == Decimal("25.50")


def test_order_mark_paid_requires_minimum_10_usd() -> None:
    """Order no puede pasar a PAID si total < 10.00 USD."""
    items = [
        OrderItem(product_id=uuid4(), quantity=1, unit_price=Money(Decimal("5.00"))),
    ]
    order = Order(customer_id=uuid4(), items=items)
    with pytest.raises(InvalidOrderStateError):
        order.mark_paid()


def test_order_mark_paid_succeeds_with_minimum() -> None:
    """Order puede pasar a PAID si total >= 10.00 USD."""
    items = [
        OrderItem(product_id=uuid4(), quantity=1, unit_price=Money(Decimal("10.00"))),
    ]
    order = Order(customer_id=uuid4(), items=items)
    order.mark_paid()
    assert order.status == OrderStatus.PAID


def test_order_cancel_from_pending() -> None:
    """Order en PENDING puede cancelarse."""
    order = Order(
        customer_id=uuid4(),
        items=[OrderItem(product_id=uuid4(), quantity=2, unit_price=Money(Decimal("10.00")))],
    )
    order.cancel()
    assert order.status == OrderStatus.CANCELLED


def test_order_cancel_from_paid() -> None:
    """Order en PAID puede cancelarse."""
    order = Order(
        customer_id=uuid4(),
        items=[OrderItem(product_id=uuid4(), quantity=2, unit_price=Money(Decimal("10.00")))],
    )
    order.mark_paid()
    order.cancel()
    assert order.status == OrderStatus.CANCELLED


def test_order_cannot_cancel_when_shipped() -> None:
    """Order en SHIPPED no puede cancelarse."""
    order = Order(
        customer_id=uuid4(),
        items=[OrderItem(product_id=uuid4(), quantity=2, unit_price=Money(Decimal("10.00")))],
    )
    order.mark_paid()
    order.mark_shipped()
    with pytest.raises(InvalidOrderStateError):
        order.cancel()


def test_order_ship_requires_paid_state() -> None:
    """Order solo puede enviarse si está en PAID."""
    order = Order(
        customer_id=uuid4(),
        items=[OrderItem(product_id=uuid4(), quantity=2, unit_price=Money(Decimal("10.00")))],
    )
    with pytest.raises(InvalidOrderStateError):
        order.mark_shipped()


def test_order_ship_from_paid() -> None:
    """Order en PAID puede enviarse."""
    order = Order(
        customer_id=uuid4(),
        items=[OrderItem(product_id=uuid4(), quantity=2, unit_price=Money(Decimal("10.00")))],
    )
    order.mark_paid()
    order.mark_shipped()
    assert order.status == OrderStatus.SHIPPED


def test_order_mark_delivered_from_shipped() -> None:
    """Order en SHIPPED puede marcarse como entregado."""
    order = Order(
        customer_id=uuid4(),
        items=[OrderItem(product_id=uuid4(), quantity=2, unit_price=Money(Decimal("10.00")))],
    )
    order.mark_paid()
    order.mark_shipped()
    order.mark_delivered()
    assert order.status == OrderStatus.DELIVERED


def test_order_has_id_and_created_at() -> None:
    """Order debe tener id (UUID) y created_at."""
    order = Order(
        customer_id=uuid4(),
        items=[OrderItem(product_id=uuid4(), quantity=1, unit_price=Money(Decimal("10.00")))],
    )
    assert order.id is not None
    assert isinstance(order.created_at, datetime)
