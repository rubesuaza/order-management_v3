"""Port de entrada para pagar un pedido."""

from abc import ABC, abstractmethod
from uuid import UUID

from order_management.domain.models.order import Order


class PayOrderPort(ABC):
    """Interfaz del caso de uso de pagar pedido."""

    @abstractmethod
    async def execute(self, order_id: UUID) -> Order:
        """Procesa el pago del pedido. Lanza OrderNotFoundError o InvalidOrderStateError."""
        ...
