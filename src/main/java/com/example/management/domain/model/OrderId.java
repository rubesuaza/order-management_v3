package com.example.management.domain.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Identity value object for Order aggregate.
 */
public final class OrderId {

    private final UUID value;

    public OrderId(UUID value) {
        this.value = Objects.requireNonNull(value, "OrderId value");
    }

    public UUID getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderId orderId = (OrderId) o;
        return Objects.equals(value, orderId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
