"""FastAPI controller (driving adapter) for getting order details."""

from uuid import UUID

from fastapi import APIRouter, Depends, HTTPException

from order_management.application.ports.in_ import GetOrderPort
from order_management.domain.exceptions import OrderNotFoundError
from order_management.infrastructure.adapters.in_.dependencies import get_get_order_port
from order_management.infrastructure.adapters.in_.schemas import OrderResponse

router = APIRouter(prefix="/orders", tags=["orders"])


@router.get("/{order_id}", response_model=OrderResponse)
async def get_order(
    order_id: UUID,
    get_order_port: GetOrderPort = Depends(get_get_order_port),
) -> OrderResponse:
    """Retrieve an order by ID."""
    try:
        order = await get_order_port.execute(order_id)
        return OrderResponse.from_order(order)
    except OrderNotFoundError as e:
        raise HTTPException(status_code=404, detail=str(e)) from e
