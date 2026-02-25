package com.example.management.application.port.in;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Input DTO for a single order line item (productId, quantity, unitPrice).
 */
public record OrderItemRequest(UUID productId, int quantity, BigDecimal unitPrice) {}
