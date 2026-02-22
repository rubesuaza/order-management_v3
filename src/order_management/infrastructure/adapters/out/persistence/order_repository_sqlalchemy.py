"""Implementación del repositorio de pedidos con SQLAlchemy async."""

from uuid import UUID

from sqlalchemy import select
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy.orm import selectinload

from order_management.application.ports.out import OrderRepository
from order_management.domain.models.order import Order

from .mappers import order_domain_to_model, order_model_to_domain
from .models import OrderItemModel, OrderModel


class OrderRepositorySqlAlchemy(OrderRepository):
    """Repositorio de pedidos persistido en PostgreSQL via SQLAlchemy."""

    def __init__(self, session: AsyncSession) -> None:
        self._session = session

    async def save(self, order: Order) -> Order:
        """Persiste un pedido (insert o update)."""
        existing = await self._session.get(OrderModel, str(order.id))
        if existing:
            # Update: actualizar campos y reemplazar items
            existing.customer_id = str(order.customer_id)
            existing.status = order.status.value
            existing.total_amount = order.total_amount.amount
            existing.currency = order.total_amount.currency
            existing.items.clear()
            for item in order.items:
                item_model = OrderItemModel(
                    order_id=str(order.id),
                    product_id=str(item.product_id),
                    quantity=item.quantity,
                    unit_price=item.unit_price.amount,
                )
                existing.items.append(item_model)
            await self._session.flush()
            await self._session.refresh(existing)
            return order_model_to_domain(existing)
        else:
            model = order_domain_to_model(order)
            self._session.add(model)
            await self._session.flush()
            await self._session.refresh(model)
            return order_model_to_domain(model)

    async def get_by_id(self, order_id: UUID) -> Order | None:
        """Obtiene un pedido por ID."""
        result = await self._session.execute(
            select(OrderModel)
            .options(selectinload(OrderModel.items))
            .where(OrderModel.id == str(order_id))
        )
        model = result.scalar_one_or_none()
        if model is None:
            return None
        return order_model_to_domain(model)

    async def exists(self, order_id: UUID) -> bool:
        """Verifica si un pedido existe."""
        order = await self.get_by_id(order_id)
        return order is not None
