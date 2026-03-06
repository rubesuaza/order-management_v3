package com.example.ordermanagement.application.ports.in;

import com.example.ordermanagement.domain.model.valueobject.OrderId;

/**
 * Common result DTO for use case operations.
 * Avoids coupling ports to domain aggregates.
 */
public record OrderResult(OrderId orderId, String status) {}
