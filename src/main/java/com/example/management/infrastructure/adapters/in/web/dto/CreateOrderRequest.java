package com.example.management.infrastructure.adapters.in.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * Request body for creating an order.
 */
public record CreateOrderRequest(
        @NotNull(message = "customerId is required")
        java.util.UUID customerId,
        @NotNull(message = "items is required")
        @Size(min = 1, message = "At least one item is required")
        @Valid
        List<OrderItemRequest> items
) {
    public record OrderItemRequest(
            @NotNull java.util.UUID productId,
            int quantity,
            java.math.BigDecimal unitPriceAmount,
            String unitPriceCurrency
    ) {}
}
