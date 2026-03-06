package com.example.ordermanagement.domain.exception;

/**
 * Base class for all domain/business exceptions.
 * Keeps domain layer isolated from framework-specific error handling.
 */
public abstract class DomainException extends RuntimeException {

    public DomainException(String message) {
        super(message);
    }

    public DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
