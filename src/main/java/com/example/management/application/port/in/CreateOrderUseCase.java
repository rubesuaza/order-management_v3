package com.example.management.application.port.in;

import com.example.management.domain.model.Order;

/**
 * Input port: create a new order with the given customer and items.
 */
public interface CreateOrderUseCase {

    /**
     * @param command create order command (customerId and items; items must not be empty)
     * @return the created order (PENDING)
     */
    Order create(CreateOrderCommand command);
}
