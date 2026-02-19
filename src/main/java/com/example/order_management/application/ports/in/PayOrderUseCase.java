package com.example.order_management.application.ports.in;

import com.example.order_management.domain.model.Order;

import java.util.UUID;

/**
 * Input port for processing order payments.
 */
public interface PayOrderUseCase {
    /**
     * Processes payment for an order.
     * @param orderId The order ID
     * @return The updated order with PAID status
     * @throws com.example.order_management.domain.exception.InvalidOrderStateException if order cannot be paid
     */
    Order payOrder(UUID orderId);
}
