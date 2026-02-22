"""Port de entrada para creación de pedidos."""

from abc import ABC, abstractmethod
from dataclasses import dataclass
from uuid import UUID

from order_management.domain.models.order import Order


@dataclass
class CreateOrderInput:
    """Datos de entrada para crear un pedido."""

    customer_id: UUID
    items: list[dict]  # [{"product_id": UUID, "quantity": int, "unit_price": Decimal}]


@dataclass
class CreateOrderResult:
    """Resultado de crear un pedido."""

    order: Order


class CreateOrderPort(ABC):
    """Interfaz del caso de uso de crear pedido."""

    @abstractmethod
    async def execute(self, input_data: CreateOrderInput) -> CreateOrderResult:
        """Ejecuta la creación del pedido."""
        ...
