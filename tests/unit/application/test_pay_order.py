"""Tests del caso de uso PayOrderUseCase."""

from decimal import Decimal
from uuid import uuid4

import pytest
from unittest.mock import AsyncMock

from order_management.application.use_cases.pay_order import PayOrderUseCase
from order_management.domain.exceptions import OrderNotFoundError, InvalidOrderStateError
from order_management.domain.models import Money, Order, OrderItem, OrderStatus


@pytest.fixture
def mock_repository() -> AsyncMock:
    """Repositorio mock para tests."""
    return AsyncMock()


@pytest.fixture
def pay_order_use_case(mock_repository: AsyncMock) -> PayOrderUseCase:
    """Instancia del caso de uso con repositorio mockeado."""
    return PayOrderUseCase(order_repository=mock_repository)


async def test_pay_order_succeeds_and_persists(
    pay_order_use_case: PayOrderUseCase,
    mock_repository: AsyncMock,
) -> None:
    """PayOrder debe marcar como pagado y persistir."""
    # Arrange
    order_id = uuid4()
    order = Order(
        id=order_id,
        customer_id=uuid4(),
        items=[OrderItem(product_id=uuid4(), quantity=2, unit_price=Money(Decimal("10.00")))],
    )
    mock_repository.get_by_id.return_value = order
    mock_repository.save.side_effect = lambda o: o

    # Act
    result = await pay_order_use_case.execute(order_id)

    # Assert
    assert result.status == OrderStatus.PAID
    mock_repository.get_by_id.assert_awaited_once_with(order_id)
    mock_repository.save.assert_awaited_once()
    saved_order = mock_repository.save.await_args[0][0]
    assert saved_order.status == OrderStatus.PAID


async def test_pay_order_raises_when_not_found(
    pay_order_use_case: PayOrderUseCase,
    mock_repository: AsyncMock,
) -> None:
    """PayOrder debe lanzar OrderNotFoundError cuando el pedido no existe."""
    # Arrange
    order_id = uuid4()
    mock_repository.get_by_id.return_value = None

    # Act & Assert
    with pytest.raises(OrderNotFoundError) as exc_info:
        await pay_order_use_case.execute(order_id)
    assert str(order_id) in str(exc_info.value)
    mock_repository.save.assert_not_awaited()


async def test_pay_order_raises_when_total_below_minimum(
    pay_order_use_case: PayOrderUseCase,
    mock_repository: AsyncMock,
) -> None:
    """PayOrder debe fallar si el total es menor a 10 USD."""
    # Arrange
    order_id = uuid4()
    order = Order(
        id=order_id,
        customer_id=uuid4(),
        items=[OrderItem(product_id=uuid4(), quantity=1, unit_price=Money(Decimal("5.00")))],
    )
    mock_repository.get_by_id.return_value = order

    # Act & Assert
    with pytest.raises(InvalidOrderStateError):
        await pay_order_use_case.execute(order_id)
    mock_repository.save.assert_not_awaited()


async def test_pay_order_raises_when_already_paid(
    pay_order_use_case: PayOrderUseCase,
    mock_repository: AsyncMock,
) -> None:
    """PayOrder debe fallar si el pedido ya está pagado."""
    # Arrange
    order_id = uuid4()
    order = Order(
        id=order_id,
        customer_id=uuid4(),
        items=[OrderItem(product_id=uuid4(), quantity=2, unit_price=Money(Decimal("10.00")))],
    )
    order.mark_paid()  # ya está PAID
    mock_repository.get_by_id.return_value = order

    # Act & Assert
    with pytest.raises(InvalidOrderStateError):
        await pay_order_use_case.execute(order_id)
