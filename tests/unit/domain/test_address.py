"""Tests del Value Object Address."""

import pytest

from order_management.domain.models.value_objects import Address


def test_address_creation() -> None:
    """Address debe crearse con todos sus atributos."""
    addr = Address(
        street="Calle Principal 123",
        city="Madrid",
        zip_code="28001",
        country="España",
    )
    assert addr.street == "Calle Principal 123"
    assert addr.city == "Madrid"
    assert addr.zip_code == "28001"
    assert addr.country == "España"


def test_address_is_immutable() -> None:
    """Address debe ser inmutable (frozen)."""
    addr = Address(street="X", city="Y", zip_code="Z", country="W")
    with pytest.raises(AttributeError):
        addr.city = "Otro"  # type: ignore[misc]
