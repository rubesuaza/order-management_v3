package com.example.order_management.application.ports.in;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Command DTO for creating an order item.
 * Technology-agnostic input structure for the application layer.
 */
public record CreateOrderItemCommand(
        UUID productId,
        int quantity,
        BigDecimal unitPrice
) {
}
