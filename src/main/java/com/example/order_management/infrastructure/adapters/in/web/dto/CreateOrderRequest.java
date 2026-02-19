package com.example.order_management.infrastructure.adapters.in.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

/**
 * DTO for creating a new order.
 */
public record CreateOrderRequest(
        @NotNull(message = "Customer ID is required")
        UUID customerId,
        
        @NotEmpty(message = "Order must have at least one item")
        @Valid
        List<OrderItemRequest> items
) {
    public record OrderItemRequest(
            @NotNull(message = "Product ID is required")
            UUID productId,
            
            @NotNull(message = "Quantity is required")
            Integer quantity,
            
            @NotNull(message = "Unit price is required")
            java.math.BigDecimal unitPrice
    ) {}
}
