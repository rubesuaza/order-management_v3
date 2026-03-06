package com.example.management.domain.model.valueobject;

/**
 * Immutable enum representing the state of an Order.
 */
public enum OrderStatus {
    PENDING,
    PAID,
    SHIPPED,
    CANCELLED
}
