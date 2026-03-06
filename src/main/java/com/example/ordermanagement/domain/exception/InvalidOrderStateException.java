package com.example.ordermanagement.domain.exception;

/**
 * Thrown when an illegal status transition is attempted on an Order.
 * E.g., cancelling a SHIPPED order or shipping a non-PAID order.
 */
public class InvalidOrderStateException extends DomainException {

    public InvalidOrderStateException(String message) {
        super(message);
    }
}
