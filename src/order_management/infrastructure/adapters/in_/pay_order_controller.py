"""FastAPI controller (driving adapter) for paying an order."""

from uuid import UUID

from fastapi import APIRouter, Depends, HTTPException

from order_management.application.ports.in_ import PayOrderPort
from order_management.domain.exceptions import InvalidOrderStateError, OrderNotFoundError
from order_management.infrastructure.adapters.in_.dependencies import get_pay_order_port
from order_management.infrastructure.adapters.in_.schemas import OrderResponse

router = APIRouter(prefix="/orders", tags=["orders"])


@router.post("/{order_id}/pay", response_model=OrderResponse)
async def pay_order(
    order_id: UUID,
    pay_order_port: PayOrderPort = Depends(get_pay_order_port),
) -> OrderResponse:
    """Process payment for an order."""
    try:
        order = await pay_order_port.execute(order_id)
        return OrderResponse.from_order(order)
    except OrderNotFoundError as e:
        raise HTTPException(status_code=404, detail=str(e)) from e
    except InvalidOrderStateError as e:
        raise HTTPException(status_code=400, detail=str(e)) from e
