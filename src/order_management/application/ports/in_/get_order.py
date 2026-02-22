"""Input port to get order details."""

from abc import ABC, abstractmethod
from uuid import UUID

from order_management.domain.models.order import Order


class GetOrderPort(ABC):
    """Interface for the get order use case."""

    @abstractmethod
    async def execute(self, order_id: UUID) -> Order:
        """Retrieves an order by ID. Raises OrderNotFoundError if it does not exist."""
        ...
