"""Pruebas unitarias del endpoint de salud."""

import pytest
from fastapi.testclient import TestClient

from order_management.main import app


@pytest.fixture
def client() -> TestClient:
    """Cliente de prueba para la API."""
    return TestClient(app)


def test_health_check_returns_ok(client: TestClient) -> None:
    """El endpoint /health debe retornar status ok."""
    response = client.get("/health")
    assert response.status_code == 200
    data = response.json()
    assert data["status"] == "ok"
    assert "version" in data


def test_root_endpoint(client: TestClient) -> None:
    """El endpoint raíz debe retornar mensaje de bienvenida."""
    response = client.get("/")
    assert response.status_code == 200
    data = response.json()
    assert "message" in data
    assert "Order Management" in data["message"]
