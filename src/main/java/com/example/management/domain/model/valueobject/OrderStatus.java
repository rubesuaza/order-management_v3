package com.example.management.domain.model.valueobject;

/**
 * Order status enum. Immutable component of Order state.
 */
public enum OrderStatus {
    PENDING,
    PAID,
    SHIPPED,
    CANCELLED
}
