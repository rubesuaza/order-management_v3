"""Tests del OrderRepositorySqlAlchemy con session mockeada."""

from datetime import datetime, timezone
from decimal import Decimal
from uuid import UUID, uuid4

import pytest
from unittest.mock import AsyncMock, MagicMock

from order_management.domain.models import Money, Order, OrderItem, OrderStatus
from order_management.infrastructure.adapters.out.persistence.models import OrderItemModel, OrderModel
from order_management.infrastructure.adapters.out.persistence.order_repository_sqlalchemy import (
    OrderRepositorySqlAlchemy,
)


@pytest.fixture
def mock_session() -> MagicMock:
    """Session de SQLAlchemy mockeada."""
    session = MagicMock()
    session.get = AsyncMock(return_value=None)
    session.add = MagicMock()  # add() es síncrono en SQLAlchemy
    session.flush = AsyncMock()
    session.refresh = AsyncMock()
    session.execute = AsyncMock()
    return session


@pytest.fixture
def repository(mock_session: MagicMock) -> OrderRepositorySqlAlchemy:
    """Repositorio con session mockeada."""
    return OrderRepositorySqlAlchemy(session=mock_session)


def _make_order(
    order_id: UUID | None = None,
    status: OrderStatus = OrderStatus.PENDING,
) -> Order:
    """Helper para crear Order de prueba."""
    oid = order_id or uuid4()
    order = Order(
        id=oid,
        customer_id=uuid4(),
        items=[OrderItem(product_id=uuid4(), quantity=2, unit_price=Money(Decimal("10.00")))],
    )
    object.__setattr__(order, "status", status)
    object.__setattr__(order, "created_at", datetime.now(timezone.utc))
    return order


def _make_order_model(order: Order) -> OrderModel:
    """Helper para crear OrderModel desde Order."""
    model = OrderModel(
        id=str(order.id),
        customer_id=str(order.customer_id),
        status=order.status.value,
        total_amount=order.total_amount.amount,
        currency=order.total_amount.currency,
        created_at=order.created_at,
    )
    for item in order.items:
        model.items.append(
            OrderItemModel(
                order_id=str(order.id),
                product_id=str(item.product_id),
                quantity=item.quantity,
                unit_price=item.unit_price.amount,
            )
        )
    return model


async def test_save_new_order_persists_and_returns(
    repository: OrderRepositorySqlAlchemy,
    mock_session: AsyncMock,
) -> None:
    """save() con pedido nuevo debe añadirlo a la session y retornar Order."""
    # Arrange
    order = _make_order()
    mock_session.get.return_value = None  # No existe -> insert
    mock_session.refresh.side_effect = lambda m: None  # refresh no modifica en mock

    # Act
    result = await repository.save(order)

    # Assert
    assert result is not None
    assert result.id == order.id
    assert result.customer_id == order.customer_id
    mock_session.add.assert_called_once()
    mock_session.flush.assert_awaited()
    mock_session.refresh.assert_awaited()


async def test_save_existing_order_updates_and_returns(
    repository: OrderRepositorySqlAlchemy,
    mock_session: AsyncMock,
) -> None:
    """save() con pedido existente debe actualizar y retornar Order."""
    # Arrange
    order = _make_order(status=OrderStatus.PAID)
    existing_model = _make_order_model(order)
    mock_session.get.return_value = existing_model

    # Act
    result = await repository.save(order)

    # Assert
    assert result is not None
    assert result.status == OrderStatus.PAID
    assert existing_model.customer_id == str(order.customer_id)
    assert existing_model.status == "PAID"
    mock_session.add.assert_not_called()
    mock_session.flush.assert_awaited()
    mock_session.refresh.assert_awaited()


async def test_get_by_id_returns_order_when_exists(
    repository: OrderRepositorySqlAlchemy,
    mock_session: AsyncMock,
) -> None:
    """get_by_id() debe retornar Order cuando existe."""
    # Arrange
    order_id = uuid4()
    order = _make_order(order_id=order_id)
    model = _make_order_model(order)
    mock_result = MagicMock()
    mock_result.scalar_one_or_none.return_value = model
    mock_session.execute = AsyncMock(return_value=mock_result)

    # Act
    result = await repository.get_by_id(order_id)

    # Assert
    assert result is not None
    assert result.id == order_id
    mock_session.execute.assert_awaited_once()


async def test_get_by_id_returns_none_when_not_exists(
    repository: OrderRepositorySqlAlchemy,
    mock_session: AsyncMock,
) -> None:
    """get_by_id() debe retornar None cuando no existe."""
    # Arrange
    order_id = uuid4()
    mock_result = MagicMock()
    mock_result.scalar_one_or_none.return_value = None
    mock_session.execute = AsyncMock(return_value=mock_result)

    # Act
    result = await repository.get_by_id(order_id)

    # Assert
    assert result is None


async def test_exists_returns_true_when_order_exists(
    repository: OrderRepositorySqlAlchemy,
    mock_session: AsyncMock,
) -> None:
    """exists() debe retornar True cuando el pedido existe."""
    # Arrange
    order_id = uuid4()
    order = _make_order(order_id=order_id)
    model = _make_order_model(order)
    mock_result = MagicMock()
    mock_result.scalar_one_or_none.return_value = model
    mock_session.execute = AsyncMock(return_value=mock_result)

    # Act
    result = await repository.exists(order_id)

    # Assert
    assert result is True


async def test_exists_returns_false_when_order_not_exists(
    repository: OrderRepositorySqlAlchemy,
    mock_session: AsyncMock,
) -> None:
    """exists() debe retornar False cuando el pedido no existe."""
    # Arrange
    order_id = uuid4()
    mock_result = MagicMock()
    mock_result.scalar_one_or_none.return_value = None
    mock_session.execute = AsyncMock(return_value=mock_result)

    # Act
    result = await repository.exists(order_id)

    # Assert
    assert result is False
