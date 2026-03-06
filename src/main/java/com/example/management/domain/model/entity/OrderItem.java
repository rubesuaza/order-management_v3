package com.example.management.domain.model.entity;

import com.example.management.domain.exception.InvalidItemException;
import com.example.management.domain.model.valueobject.Money;
import lombok.Builder;

import java.util.Objects;
import java.util.UUID;

/**
 * Entity representing an item within an Order aggregate.
 * Must only be instantiated or modified through the Order aggregate.
 */
public class OrderItem {

    private final UUID id;
    private final String productId;
    private final int quantity;
    private final Money unitPrice;

    @Builder
    public OrderItem(UUID id, String productId, int quantity, Money unitPrice) {
        if (productId == null || productId.isBlank()) {
            throw new InvalidItemException("Product ID cannot be null or blank");
        }
        if (quantity <= 0) {
            throw new InvalidItemException("Quantity must be greater than 0");
        }
        if (unitPrice == null) {
            throw new InvalidItemException("Unit price cannot be null");
        }
        if (unitPrice.getAmount().compareTo(java.math.BigDecimal.ZERO) < 0) {
            throw new InvalidItemException("Unit price cannot be negative");
        }
        this.id = id != null ? id : UUID.randomUUID();
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public OrderItem(String productId, int quantity, Money unitPrice) {
        this(UUID.randomUUID(), productId, quantity, unitPrice);
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
