package com.example.management.domain.exception;

/**
 * Thrown when an illegal status transition is attempted on an Order.
 * E.g., cancelling a SHIPPED order, or shipping a PENDING order.
 */
public class InvalidOrderStateException extends DomainException {

    public InvalidOrderStateException(String message) {
        super(message);
    }
}
