package com.example.order_management.application.ports.in;

import com.example.order_management.domain.model.Order;

import java.util.Optional;
import java.util.UUID;

/**
 * Input port for retrieving orders.
 */
public interface GetOrderUseCase {
    /**
     * Retrieves an order by its ID.
     * @param orderId The order ID
     * @return Optional containing the order if found, empty otherwise
     */
    Optional<Order> getOrderById(UUID orderId);
}
