package com.example.management.domain.exception;

/**
 * Thrown for quantity or negative price errors in OrderItem.
 * E.g., quantity <= 0 or unitPrice < 0.
 */
public class InvalidItemException extends DomainException {

    public InvalidItemException(String message) {
        super(message);
    }
}
