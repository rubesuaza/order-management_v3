package com.example.management.application.port.in;

import java.util.List;
import java.util.UUID;

/**
 * Command encapsulating all input parameters for creating an order.
 */
public record CreateOrderCommand(UUID customerId, List<OrderItemRequest> items) {}
