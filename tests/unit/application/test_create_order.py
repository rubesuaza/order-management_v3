"""Tests del caso de uso CreateOrderUseCase."""

from decimal import Decimal
from uuid import uuid4

import pytest
from unittest.mock import AsyncMock, MagicMock

from order_management.application.ports.in_.create_order import CreateOrderInput, CreateOrderResult
from order_management.application.use_cases.create_order import CreateOrderUseCase
from order_management.domain.exceptions import InvalidItemError
from order_management.domain.models import Money, Order, OrderItem, OrderStatus


@pytest.fixture
def mock_repository() -> AsyncMock:
    """Repositorio mock para tests."""
    return AsyncMock()


@pytest.fixture
def create_order_use_case(mock_repository: AsyncMock) -> CreateOrderUseCase:
    """Instancia del caso de uso con repositorio mockeado."""
    return CreateOrderUseCase(order_repository=mock_repository)


async def test_create_order_succeeds_and_persists(
    create_order_use_case: CreateOrderUseCase,
    mock_repository: AsyncMock,
) -> None:
    """CreateOrder debe crear el pedido y persistirlo via repositorio."""
    # Arrange
    customer_id = uuid4()
    product_id = uuid4()
    input_data = CreateOrderInput(
        customer_id=customer_id,
        items=[
            {"product_id": product_id, "quantity": 2, "unit_price": "10.50"},
        ],
    )
    saved_order = Order(
        id=uuid4(),
        customer_id=customer_id,
        items=[
            OrderItem(product_id=product_id, quantity=2, unit_price=Money(Decimal("10.50"))),
        ],
    )
    mock_repository.save.return_value = saved_order

    # Act
    result = await create_order_use_case.execute(input_data)

    # Assert
    assert isinstance(result, CreateOrderResult)
    assert result.order == saved_order
    mock_repository.save.assert_awaited_once()
    call_order = mock_repository.save.await_args[0][0]
    assert call_order.customer_id == customer_id
    assert len(call_order.items) == 1
    assert call_order.items[0].product_id == product_id
    assert call_order.items[0].quantity == 2
    assert call_order.items[0].unit_price.amount == Decimal("10.50")
    assert call_order.status == OrderStatus.PENDING


async def test_create_order_with_multiple_items(
    create_order_use_case: CreateOrderUseCase,
    mock_repository: AsyncMock,
) -> None:
    """CreateOrder debe soportar múltiples ítems."""
    # Arrange
    customer_id = uuid4()
    product1_id = uuid4()
    product2_id = uuid4()
    input_data = CreateOrderInput(
        customer_id=customer_id,
        items=[
            {"product_id": product1_id, "quantity": 1, "unit_price": "25.00"},
            {"product_id": product2_id, "quantity": 3, "unit_price": "5.00"},
        ],
    )
    mock_repository.save.side_effect = lambda o: o  # retorna el mismo order

    # Act
    result = await create_order_use_case.execute(input_data)

    # Assert
    call_order = mock_repository.save.await_args[0][0]
    assert len(call_order.items) == 2
    assert call_order.total_amount.amount == Decimal("40.00")  # 25 + 15


async def test_create_order_with_custom_currency(
    create_order_use_case: CreateOrderUseCase,
    mock_repository: AsyncMock,
) -> None:
    """CreateOrder debe aceptar currency en ítems."""
    # Arrange
    customer_id = uuid4()
    product_id = uuid4()
    input_data = CreateOrderInput(
        customer_id=customer_id,
        items=[
            {"product_id": product_id, "quantity": 1, "unit_price": "10.00", "currency": "EUR"},
        ],
    )
    mock_repository.save.side_effect = lambda o: o

    # Act
    result = await create_order_use_case.execute(input_data)

    # Assert
    call_order = mock_repository.save.await_args[0][0]
    assert call_order.items[0].unit_price.currency == "EUR"


async def test_create_order_with_empty_items_raises(
    create_order_use_case: CreateOrderUseCase,
    mock_repository: AsyncMock,
) -> None:
    """CreateOrder debe fallar si no hay ítems (Order rechaza items vacíos)."""
    # Arrange
    input_data = CreateOrderInput(
        customer_id=uuid4(),
        items=[],
    )

    # Act & Assert
    with pytest.raises(InvalidItemError):
        await create_order_use_case.execute(input_data)
    mock_repository.save.assert_not_awaited()


async def test_create_order_with_invalid_item_quantity_raises(
    create_order_use_case: CreateOrderUseCase,
    mock_repository: AsyncMock,
) -> None:
    """CreateOrder debe fallar si un ítem tiene cantidad <= 0."""
    # Arrange
    input_data = CreateOrderInput(
        customer_id=uuid4(),
        items=[{"product_id": uuid4(), "quantity": 0, "unit_price": "10.00"}],
    )

    # Act & Assert
    with pytest.raises(InvalidItemError):
        await create_order_use_case.execute(input_data)
    mock_repository.save.assert_not_awaited()


async def test_create_order_with_negative_price_raises(
    create_order_use_case: CreateOrderUseCase,
    mock_repository: AsyncMock,
) -> None:
    """CreateOrder debe fallar si un ítem tiene precio negativo."""
    # Arrange
    input_data = CreateOrderInput(
        customer_id=uuid4(),
        items=[{"product_id": uuid4(), "quantity": 1, "unit_price": "-5.00"}],
    )

    # Act & Assert
    with pytest.raises(InvalidItemError):
        await create_order_use_case.execute(input_data)
    mock_repository.save.assert_not_awaited()
