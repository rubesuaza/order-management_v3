package com.example.order_management.domain.exception;

import java.util.UUID;

/**
 * Exception thrown when an Order is not found.
 */
public class OrderNotFoundException extends DomainException {
    
    public OrderNotFoundException(UUID orderId) {
        super(String.format("Order not found: %s", orderId));
    }
    
    public OrderNotFoundException(String message) {
        super(message);
    }
}
