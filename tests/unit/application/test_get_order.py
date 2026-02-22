"""Tests del caso de uso GetOrderUseCase."""

from decimal import Decimal
from uuid import uuid4

import pytest
from unittest.mock import AsyncMock

from order_management.application.use_cases.get_order import GetOrderUseCase
from order_management.domain.exceptions import OrderNotFoundError
from order_management.domain.models import Money, Order, OrderItem


@pytest.fixture
def mock_repository() -> AsyncMock:
    """Repositorio mock para tests."""
    return AsyncMock()


@pytest.fixture
def get_order_use_case(mock_repository: AsyncMock) -> GetOrderUseCase:
    """Instancia del caso de uso con repositorio mockeado."""
    return GetOrderUseCase(order_repository=mock_repository)


async def test_get_order_returns_order_when_exists(
    get_order_use_case: GetOrderUseCase,
    mock_repository: AsyncMock,
) -> None:
    """GetOrder debe retornar el pedido cuando existe."""
    # Arrange
    order_id = uuid4()
    order = Order(
        id=order_id,
        customer_id=uuid4(),
        items=[OrderItem(product_id=uuid4(), quantity=1, unit_price=Money(Decimal("10.00")))],
    )
    mock_repository.get_by_id.return_value = order

    # Act
    result = await get_order_use_case.execute(order_id)

    # Assert
    assert result == order
    mock_repository.get_by_id.assert_awaited_once_with(order_id)


async def test_get_order_raises_when_not_found(
    get_order_use_case: GetOrderUseCase,
    mock_repository: AsyncMock,
) -> None:
    """GetOrder debe lanzar OrderNotFoundError cuando el pedido no existe."""
    # Arrange
    order_id = uuid4()
    mock_repository.get_by_id.return_value = None

    # Act & Assert
    with pytest.raises(OrderNotFoundError) as exc_info:
        await get_order_use_case.execute(order_id)
    assert str(order_id) in str(exc_info.value)
    mock_repository.get_by_id.assert_awaited_once_with(order_id)
