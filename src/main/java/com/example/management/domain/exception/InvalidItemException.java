package com.example.management.domain.exception;

/**
 * Thrown for quantity or negative price errors in OrderItem.
 */
public class InvalidItemException extends DomainException {

    public InvalidItemException(String message) {
        super(message);
    }
}
