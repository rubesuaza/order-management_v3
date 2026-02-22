"""Pydantic schemas for API request/response."""

from decimal import Decimal
from uuid import UUID

from pydantic import BaseModel, Field


class OrderItemRequest(BaseModel):
    """Request schema for a single order item."""

    product_id: UUID
    quantity: int = Field(..., gt=0, description="Quantity must be positive")
    unit_price: Decimal = Field(..., ge=0, description="Unit price cannot be negative")
    currency: str = "USD"


class CreateOrderRequest(BaseModel):
    """Request schema for creating an order."""

    customer_id: UUID
    items: list[OrderItemRequest] = Field(..., min_length=1, description="At least one item required")


class OrderItemResponse(BaseModel):
    """Response schema for an order item."""

    product_id: UUID
    quantity: int
    unit_price: Decimal
    currency: str


class OrderResponse(BaseModel):
    """Response schema for an order."""

    id: UUID
    customer_id: UUID
    status: str
    total_amount: Decimal
    currency: str
    items: list[OrderItemResponse]
    created_at: str

    @classmethod
    def from_order(cls, order) -> "OrderResponse":
        """Build OrderResponse from domain Order."""
        return cls(
            id=order.id,
            customer_id=order.customer_id,
            status=order.status.value,
            total_amount=order.total_amount.amount,
            currency=order.total_amount.currency,
            items=[
                OrderItemResponse(
                    product_id=item.product_id,
                    quantity=item.quantity,
                    unit_price=item.unit_price.amount,
                    currency=item.unit_price.currency,
                )
                for item in order.items
            ],
            created_at=order.created_at.isoformat(),
        )
