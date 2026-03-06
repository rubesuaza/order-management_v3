package com.example.management.application.ports.in;

import com.example.management.domain.model.aggregate.Order;

import java.util.List;

/**
 * UC-ORD-01: Create Order. Input port for order creation use case.
 */
public interface CreateOrderUseCase {

    /**
     * Creates a new order with the given items. Order is placed (validated) and saved.
     *
     * @param items list of items (productId, quantity, unitPrice)
     * @return the created Order
     */
    Order createOrder(List<OrderItemRequest> items);

    record OrderItemRequest(String productId, int quantity, java.math.BigDecimal unitPrice, String currency) {}
}
