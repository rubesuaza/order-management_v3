package com.example.management.domain.model;

import com.example.management.domain.exception.InvalidItemException;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

/**
 * An item within an order: product reference, quantity, and unit price.
 */
public final class OrderItem {

    private final UUID productId;
    private final int quantity;
    private final Money unitPrice;

    public OrderItem(UUID productId, int quantity, Money unitPrice) {
        this.productId = Objects.requireNonNull(productId, "productId");
        this.quantity = quantity;
        this.unitPrice = Objects.requireNonNull(unitPrice, "unitPrice");
        validate();
    }

    private void validate() {
        if (quantity <= 0) {
            throw new InvalidItemException("Quantity must be strictly greater than zero");
        }
        if (unitPrice.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidItemException("Unit price cannot be negative");
        }
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
