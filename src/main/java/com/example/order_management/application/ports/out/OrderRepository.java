package com.example.order_management.application.ports.out;

import com.example.order_management.domain.model.Order;

import java.util.Optional;
import java.util.UUID;

/**
 * Output port for order persistence operations.
 * Defines the contract for saving and retrieving orders.
 */
public interface OrderRepository {
    /**
     * Saves an order.
     * @param order The order to save
     * @return The saved order
     */
    Order save(Order order);

    /**
     * Finds an order by its ID.
     * @param orderId The order ID
     * @return Optional containing the order if found, empty otherwise
     */
    Optional<Order> findById(UUID orderId);
}
