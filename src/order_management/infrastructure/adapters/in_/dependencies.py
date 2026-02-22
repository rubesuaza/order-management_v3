"""Dependency injection for inbound adapters."""

from fastapi import Depends
from sqlalchemy.ext.asyncio import AsyncSession

from order_management.application.ports.in_ import CreateOrderPort, GetOrderPort, PayOrderPort
from order_management.application.ports.out import OrderRepository
from order_management.application.use_cases.create_order import CreateOrderUseCase
from order_management.application.use_cases.get_order import GetOrderUseCase
from order_management.application.use_cases.pay_order import PayOrderUseCase
from order_management.infrastructure.adapters.out.persistence.order_repository_sqlalchemy import (
    OrderRepositorySqlAlchemy,
)
from order_management.infrastructure.config.database import get_db_session


async def get_create_order_port(
    session: AsyncSession = Depends(get_db_session),
) -> CreateOrderPort:
    """Provide CreateOrderPort implementation with repository."""
    repository: OrderRepository = OrderRepositorySqlAlchemy(session)
    return CreateOrderUseCase(order_repository=repository)


async def get_get_order_port(
    session: AsyncSession = Depends(get_db_session),
) -> GetOrderPort:
    """Provide GetOrderPort implementation with repository."""
    repository: OrderRepository = OrderRepositorySqlAlchemy(session)
    return GetOrderUseCase(order_repository=repository)


async def get_pay_order_port(
    session: AsyncSession = Depends(get_db_session),
) -> PayOrderPort:
    """Provide PayOrderPort implementation with repository."""
    repository: OrderRepository = OrderRepositorySqlAlchemy(session)
    return PayOrderUseCase(order_repository=repository)
