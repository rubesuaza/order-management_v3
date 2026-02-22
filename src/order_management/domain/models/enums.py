"""Enumeraciones del dominio."""

from enum import Enum


class OrderStatus(str, Enum):
    """Estados posibles de un pedido."""

    PENDING = "PENDING"
    PAID = "PAID"
    SHIPPED = "SHIPPED"
    DELIVERED = "DELIVERED"
    CANCELLED = "CANCELLED"
