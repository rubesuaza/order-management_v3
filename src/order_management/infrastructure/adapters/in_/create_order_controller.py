"""FastAPI controller (driving adapter) for order creation."""

from fastapi import APIRouter, Depends, HTTPException

from order_management.application.ports.in_ import CreateOrderInput, CreateOrderPort, OrderItemInput
from order_management.domain.exceptions import InvalidItemError
from order_management.infrastructure.adapters.in_.dependencies import get_create_order_port
from order_management.infrastructure.adapters.in_.schemas import (
    CreateOrderRequest,
    OrderResponse,
)

router = APIRouter(prefix="/orders", tags=["orders"])


@router.post("", status_code=201, response_model=OrderResponse)
async def create_order(
    request: CreateOrderRequest,
    create_order_port: CreateOrderPort = Depends(get_create_order_port),
) -> OrderResponse:
    """Create a new order."""
    try:
        input_data = CreateOrderInput(
            customer_id=request.customer_id,
            items=[
                OrderItemInput(
                    product_id=item.product_id,
                    quantity=item.quantity,
                    unit_price=item.unit_price,
                    currency=item.currency,
                )
                for item in request.items
            ],
        )
        result = await create_order_port.execute(input_data)
        return OrderResponse.from_order(result.order)
    except InvalidItemError as e:
        raise HTTPException(status_code=400, detail=str(e)) from e
