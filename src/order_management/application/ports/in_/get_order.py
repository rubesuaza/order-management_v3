"""Port de entrada para obtener detalle de pedido."""

from abc import ABC, abstractmethod
from uuid import UUID

from order_management.domain.models.order import Order


class GetOrderPort(ABC):
    """Interfaz del caso de uso de obtener pedido."""

    @abstractmethod
    async def execute(self, order_id: UUID) -> Order:
        """Obtiene un pedido por ID. Lanza OrderNotFoundError si no existe."""
        ...
