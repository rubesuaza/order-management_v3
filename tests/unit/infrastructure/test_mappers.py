"""Tests de los mappers dominio <-> persistencia."""

from datetime import datetime, timezone
from decimal import Decimal
from uuid import uuid4

from order_management.domain.models import Money, Order, OrderItem, OrderStatus
from order_management.infrastructure.adapters.out.persistence.mappers import (
    order_domain_to_model,
    order_model_to_domain,
)
from order_management.infrastructure.adapters.out.persistence.models import (
    Base,
    OrderItemModel,
    OrderModel,
)


def test_order_domain_to_model() -> None:
    """order_domain_to_model debe convertir Order a OrderModel."""
    # Arrange
    order_id = uuid4()
    customer_id = uuid4()
    product_id = uuid4()
    created_at = datetime.now(timezone.utc)
    order = Order(
        id=order_id,
        customer_id=customer_id,
        items=[
            OrderItem(
                product_id=product_id,
                quantity=2,
                unit_price=Money(Decimal("10.50")),
            ),
        ],
    )
    object.__setattr__(order, "created_at", created_at)

    # Act
    model = order_domain_to_model(order)

    # Assert
    assert isinstance(model, OrderModel)
    assert model.id == str(order_id)
    assert model.customer_id == str(customer_id)
    assert model.status == "PENDING"
    assert model.total_amount == Decimal("21.00")
    assert model.currency == "USD"
    assert model.created_at == created_at
    assert len(model.items) == 1
    assert model.items[0].product_id == str(product_id)
    assert model.items[0].quantity == 2
    assert model.items[0].unit_price == Decimal("10.50")


def test_order_model_to_domain() -> None:
    """order_model_to_domain debe convertir OrderModel a Order."""
    # Arrange
    order_id = uuid4()
    customer_id = uuid4()
    product_id = uuid4()
    created_at = datetime.now(timezone.utc)
    model = OrderModel(
        id=str(order_id),
        customer_id=str(customer_id),
        status="PAID",
        total_amount=Decimal("21.00"),
        currency="USD",
        created_at=created_at,
    )
    item_model = OrderItemModel(
        order_id=str(order_id),
        product_id=str(product_id),
        quantity=2,
        unit_price=Decimal("10.50"),
    )
    model.items.append(item_model)

    # Act
    order = order_model_to_domain(model)

    # Assert
    assert isinstance(order, Order)
    assert order.id == order_id
    assert order.customer_id == customer_id
    assert order.status == OrderStatus.PAID
    assert order.total_amount.amount == Decimal("21.00")
    assert order.total_amount.currency == "USD"
    assert order.created_at == created_at
    assert len(order.items) == 1
    assert order.items[0].product_id == product_id
    assert order.items[0].quantity == 2
    assert order.items[0].unit_price.amount == Decimal("10.50")


def test_order_domain_to_model_and_back_roundtrip() -> None:
    """Conversión dominio -> modelo -> dominio debe preservar datos."""
    # Arrange
    order = Order(
        id=uuid4(),
        customer_id=uuid4(),
        items=[
            OrderItem(
                product_id=uuid4(),
                quantity=3,
                unit_price=Money(Decimal("7.25")),
            ),
        ],
    )
    object.__setattr__(order, "status", OrderStatus.SHIPPED)

    # Act
    model = order_domain_to_model(order)
    restored = order_model_to_domain(model)

    # Assert
    assert restored.id == order.id
    assert restored.customer_id == order.customer_id
    assert restored.status == order.status
    assert restored.total_amount.amount == order.total_amount.amount
    assert len(restored.items) == len(order.items)
    for r_item, o_item in zip(restored.items, order.items):
        assert r_item.product_id == o_item.product_id
        assert r_item.quantity == o_item.quantity
        assert r_item.unit_price.amount == o_item.unit_price.amount
