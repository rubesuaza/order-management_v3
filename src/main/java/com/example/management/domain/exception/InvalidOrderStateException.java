package com.example.management.domain.exception;

/**
 * Thrown for illegal status transitions (e.g., cancel when SHIPPED, ship when not PAID).
 */
public class InvalidOrderStateException extends DomainException {

    public InvalidOrderStateException(String message) {
        super(message);
    }
}
