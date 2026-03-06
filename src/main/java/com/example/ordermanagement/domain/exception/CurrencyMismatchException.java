package com.example.ordermanagement.domain.exception;

/**
 * Thrown when Money operations involve different currencies.
 */
public class CurrencyMismatchException extends DomainException {

    public CurrencyMismatchException(String message) {
        super(message);
    }
}
