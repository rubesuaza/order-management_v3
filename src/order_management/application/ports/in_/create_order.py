"""Input port for order creation."""

from abc import ABC, abstractmethod
from dataclasses import dataclass
from decimal import Decimal
from uuid import UUID

from order_management.domain.models.order import Order


@dataclass
class OrderItemInput:
    """Input structure for a single order item."""

    product_id: UUID
    quantity: int
    unit_price: Decimal
    currency: str = "USD"


@dataclass
class CreateOrderInput:
    """Input data for creating an order."""

    customer_id: UUID
    items: list[OrderItemInput]


@dataclass
class CreateOrderResult:
    """Result of creating an order."""

    order: Order


class CreateOrderPort(ABC):
    """Interface for the create order use case."""

    @abstractmethod
    async def execute(self, input_data: CreateOrderInput) -> CreateOrderResult:
        """Executes order creation."""
        ...
