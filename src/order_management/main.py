"""
FastAPI entry point for Order Management.
"""

from fastapi import FastAPI

from order_management import __version__
from order_management.infrastructure.config.settings import get_settings
from order_management.infrastructure.adapters.in_.create_order_controller import router as create_order_router
from order_management.infrastructure.adapters.in_.get_order_controller import router as get_order_router
from order_management.infrastructure.adapters.in_.pay_order_controller import router as pay_order_router

app = FastAPI(
    title="Order Management",
    description="Order management API - Hexagonal Architecture",
    version=__version__,
)

api_prefix = get_settings().api_prefix
app.include_router(create_order_router, prefix=api_prefix)
app.include_router(get_order_router, prefix=api_prefix)
app.include_router(pay_order_router, prefix=api_prefix)


@app.get("/health")
async def health_check() -> dict[str, str]:
    """Health endpoint to verify the API is operational."""
    return {"status": "ok", "version": __version__}


@app.get("/")
async def root() -> dict[str, str]:
    """Root endpoint."""
    return {"message": "Order Management API", "docs": "/docs"}
