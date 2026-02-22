"""
Punto de entrada FastAPI para Order Management.
"""

from fastapi import FastAPI

from order_management import __version__

app = FastAPI(
    title="Order Management",
    description="API de gestión de pedidos - Arquitectura Hexagonal",
    version=__version__,
)


@app.get("/health")
async def health_check() -> dict[str, str]:
    """Endpoint de salud para verificar que la API está operativa."""
    return {"status": "ok", "version": __version__}


@app.get("/")
async def root() -> dict[str, str]:
    """Endpoint raíz."""
    return {"message": "Order Management API", "docs": "/docs"}
