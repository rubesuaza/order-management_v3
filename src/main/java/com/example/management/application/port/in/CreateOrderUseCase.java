package com.example.management.application.port.in;

import com.example.management.domain.model.Order;

import java.util.List;
import java.util.UUID;

/**
 * Input port: create a new order with the given customer and items.
 */
public interface CreateOrderUseCase {

    /**
     * @param customerId customer identifier
     * @param items      list of (productId, quantity, unitPrice); must not be empty
     * @return the created order (PENDING)
     */
    Order create(UUID customerId, List<OrderItemRequest> items);

    record OrderItemRequest(UUID productId, int quantity, java.math.BigDecimal unitPrice) {}
}
