package com.example.ordermanagement.domain.model.entity;

import com.example.ordermanagement.domain.exception.InvalidItemException;
import com.example.ordermanagement.domain.model.valueobject.Money;

import java.util.Objects;
import java.util.UUID;

/**
 * Entity representing a line item within an Order aggregate.
 * Must only be instantiated or modified through the Order aggregate.
 */
public final class OrderItem {

    private final UUID id;
    private final String productId;
    private final int quantity;
    private final Money unitPrice;

    public OrderItem(UUID id, String productId, int quantity, Money unitPrice) {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
        if (productId == null || productId.isBlank()) {
            throw new IllegalArgumentException("productId cannot be null or blank");
        }
        if (quantity <= 0) {
            throw new InvalidItemException("Quantity must be greater than 0, got: " + quantity);
        }
        if (unitPrice == null) {
            throw new IllegalArgumentException("unitPrice cannot be null");
        }
        if (unitPrice.getAmount().compareTo(java.math.BigDecimal.ZERO) < 0) {
            throw new InvalidItemException("Unit price cannot be negative");
        }
        this.id = id;
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public UUID getId() {
        return id;
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
        return unitPrice.multiply(quantity);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderItem orderItem = (OrderItem) o;
        return Objects.equals(id, orderItem.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
