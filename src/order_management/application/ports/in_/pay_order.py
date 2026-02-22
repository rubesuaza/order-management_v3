"""Input port for paying an order."""

from abc import ABC, abstractmethod
from uuid import UUID

from order_management.domain.models.order import Order


class PayOrderPort(ABC):
    """Interface for the pay order use case."""

    @abstractmethod
    async def execute(self, order_id: UUID) -> Order:
        """Processes order payment. Raises OrderNotFoundError or InvalidOrderStateError."""
        ...
