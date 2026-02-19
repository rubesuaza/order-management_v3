package com.example.order_management.domain.exception;

/**
 * Exception thrown when an Order has invalid attributes or violates business rules.
 */
public class InvalidOrderException extends DomainException {
    
    public InvalidOrderException(String message) {
        super(message);
    }
}
