package com.example.management.domain.model;

import com.example.management.domain.exception.InvalidItemException;
import com.example.management.domain.valueobject.Money;

import java.util.Objects;
import java.util.UUID;

/**
 * Order line item. Quantity must be strictly positive; unit price must be non-negative.
 */
public class OrderItem {

    private final UUID productId;
    private final int quantity;
    private final Money unitPrice;

    public OrderItem(UUID productId, int quantity, Money unitPrice) {
        if (quantity <= 0) {
            throw new InvalidItemException("Order item quantity must be strictly greater than zero");
        }
        if (unitPrice == null || unitPrice.getAmount().compareTo(java.math.BigDecimal.ZERO) < 0) {
            throw new InvalidItemException("Order item unit price cannot be negative");
        }
        this.productId = Objects.requireNonNull(productId, "productId");
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public UUID getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public Money getUnitPrice() {
        return unitPrice;
    }

    /**
     * Line total = unitPrice * quantity.
     */
    public Money getLineTotal() {
        return unitPrice.multiply(quantity);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderItem orderItem = (OrderItem) o;
        return quantity == orderItem.quantity
                && Objects.equals(productId, orderItem.productId)
                && Objects.equals(unitPrice, orderItem.unitPrice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, quantity, unitPrice);
    }
}
