"""Port de salida para persistencia de pedidos."""

from abc import ABC, abstractmethod
from uuid import UUID

from order_management.domain.models.order import Order


class OrderRepository(ABC):
    """Interfaz del repositorio de pedidos."""

    @abstractmethod
    async def save(self, order: Order) -> Order:
        """Persiste un pedido y retorna la instancia actualizada."""
        ...

    @abstractmethod
    async def get_by_id(self, order_id: UUID) -> Order | None:
        """Obtiene un pedido por ID. Retorna None si no existe."""
        ...

    @abstractmethod
    async def exists(self, order_id: UUID) -> bool:
        """Verifica si un pedido existe."""
        ...
