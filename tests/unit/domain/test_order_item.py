"""Tests de la entidad OrderItem."""

from decimal import Decimal
from uuid import uuid4

import pytest

from order_management.domain.exceptions import InvalidItemError
from order_management.domain.models import Money, OrderItem


def test_order_item_creation_valid() -> None:
    """OrderItem debe crearse con cantidad y precio válidos."""
    product_id = uuid4()
    item = OrderItem(product_id=product_id, quantity=2, unit_price=Money(Decimal("10.00")))
    assert item.product_id == product_id
    assert item.quantity == 2
    assert item.unit_price.amount == Decimal("10.00")


def test_order_item_quantity_must_be_positive() -> None:
    """OrderItem debe rechazar cantidad <= 0."""
    product_id = uuid4()
    with pytest.raises(InvalidItemError):
        OrderItem(product_id=product_id, quantity=0, unit_price=Money(Decimal("10.00")))
    with pytest.raises(InvalidItemError):
        OrderItem(product_id=product_id, quantity=-1, unit_price=Money(Decimal("10.00")))


def test_order_item_unit_price_cannot_be_negative() -> None:
    """OrderItem debe rechazar unit_price negativo."""
    product_id = uuid4()
    with pytest.raises(InvalidItemError):
        OrderItem(product_id=product_id, quantity=1, unit_price=Money(Decimal("-5.00")))


def test_order_item_line_total() -> None:
    """OrderItem debe calcular el total de línea (unit_price * quantity)."""
    item = OrderItem(
        product_id=uuid4(),
        quantity=3,
        unit_price=Money(Decimal("2.50")),
    )
    assert item.line_total.amount == Decimal("7.50")


def test_order_item_zero_price_allowed() -> None:
    """OrderItem debe permitir unit_price cero (producto gratuito)."""
    item = OrderItem(
        product_id=uuid4(),
        quantity=1,
        unit_price=Money(Decimal("0")),
    )
    assert item.line_total.amount == Decimal("0")
