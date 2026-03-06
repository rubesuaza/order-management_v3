package com.example.management.domain.exception;

/**
 * Base class for all business/domain errors.
 * Domain exceptions are isolated from models to keep the model package clean.
 */
public abstract class DomainException extends RuntimeException {

    protected DomainException(String message) {
        super(message);
    }

    protected DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
