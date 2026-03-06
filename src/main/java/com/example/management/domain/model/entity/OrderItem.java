package com.example.management.domain.model.entity;

import com.example.management.domain.exception.InvalidItemException;
import com.example.management.domain.model.valueobject.Money;

import java.util.Objects;

/**
 * Entity representing an item within an Order aggregate.
 * Must only be instantiated or modified through the Order aggregate.
 * Validates quantity > 0 and non-negative unitPrice during construction.
 */
public final class OrderItem {

    private final int itemId;
    private final String productId;
    private final int quantity;
    private final Money unitPrice;

    public OrderItem(int itemId, String productId, int quantity, Money unitPrice) {
        if (quantity <= 0) {
            throw new InvalidItemException("Quantity must be greater than 0, got: " + quantity);
        }
        if (unitPrice == null) {
            throw new InvalidItemException("Unit price cannot be null");
        }
        if (unitPrice.getAmount().compareTo(java.math.BigDecimal.ZERO) < 0) {
            throw new InvalidItemException("Unit price cannot be negative");
        }
        if (productId == null || productId.isBlank()) {
            throw new InvalidItemException("Product ID cannot be null or blank");
        }
        this.itemId = itemId;
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public int getItemId() {
        return itemId;
    }

    public String getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public Money getUnitPrice() {
        return unitPrice;
    }

    public Money getLineTotal() {
        return unitPrice.multiply(java.math.BigDecimal.valueOf(quantity));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderItem orderItem = (OrderItem) o;
        return itemId == orderItem.itemId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemId);
    }
}
