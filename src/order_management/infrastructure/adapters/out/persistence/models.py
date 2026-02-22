"""Modelos SQLAlchemy para persistencia."""

from datetime import datetime
from decimal import Decimal
from uuid import uuid4

from sqlalchemy import DateTime, ForeignKey, Numeric, String
from sqlalchemy.dialects.postgresql import UUID
from sqlalchemy.orm import DeclarativeBase, Mapped, mapped_column, relationship


class Base(DeclarativeBase):
    """Base para todos los modelos SQLAlchemy."""

    pass


class OrderModel(Base):
    """Modelo de persistencia para la entidad Order."""

    __tablename__ = "orders"

    id: Mapped[str] = mapped_column(
        UUID(as_uuid=False),
        primary_key=True,
        default=lambda: str(uuid4()),
    )
    customer_id: Mapped[str] = mapped_column(UUID(as_uuid=False), nullable=False, index=True)
    status: Mapped[str] = mapped_column(String(20), nullable=False, index=True)
    total_amount: Mapped[Decimal] = mapped_column(Numeric(19, 2), nullable=False)
    currency: Mapped[str] = mapped_column(String(3), nullable=False)
    created_at: Mapped[datetime] = mapped_column(DateTime(timezone=True), nullable=False)

    items: Mapped[list["OrderItemModel"]] = relationship(
        "OrderItemModel",
        back_populates="order",
        cascade="all, delete-orphan",
    )


class OrderItemModel(Base):
    """Modelo de persistencia para OrderItem."""

    __tablename__ = "order_items"

    id: Mapped[str] = mapped_column(
        UUID(as_uuid=False),
        primary_key=True,
        default=lambda: str(uuid4()),
    )
    order_id: Mapped[str] = mapped_column(
        UUID(as_uuid=False),
        ForeignKey("orders.id", ondelete="CASCADE"),
        nullable=False,
    )
    product_id: Mapped[str] = mapped_column(UUID(as_uuid=False), nullable=False)
    quantity: Mapped[int] = mapped_column(nullable=False)
    unit_price: Mapped[Decimal] = mapped_column(Numeric(19, 2), nullable=False)

    order: Mapped["OrderModel"] = relationship("OrderModel", back_populates="items")
