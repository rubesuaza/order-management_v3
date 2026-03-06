package com.example.ordermanagement.domain.model.valueobject;

/**
 * Immutable enum representing Order lifecycle states.
 */
public enum OrderStatus {
    PENDING,
    PAID,
    SHIPPED,
    CANCELLED
}
