package com.example.order_management.infrastructure.adapters.in.web.dto;

import java.util.UUID;

/**
 * DTO for pay order response.
 */
public record PayOrderResponse(
        UUID orderId,
        String status
) {}
