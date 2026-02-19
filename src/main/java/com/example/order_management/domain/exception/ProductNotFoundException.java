package com.example.order_management.domain.exception;

import java.util.UUID;

/**
 * Exception thrown when a Product is not found.
 */
public class ProductNotFoundException extends DomainException {
    
    public ProductNotFoundException(UUID productId) {
        super(String.format("Product not found: %s", productId));
    }
    
    public ProductNotFoundException(String message) {
        super(message);
    }
}
