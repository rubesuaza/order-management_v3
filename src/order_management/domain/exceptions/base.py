"""Excepciones base del dominio de gestión de pedidos."""


class DomainError(Exception):
    """Clase base para todas las excepciones del dominio."""


class InvalidOrderStateError(DomainError):
    """Error para transiciones de estado ilegales del pedido."""


class InvalidItemError(DomainError):
    """Error para errores de cantidad o precio en ítems."""


class CurrencyMismatchError(DomainError):
    """Error para operaciones con monedas diferentes."""
