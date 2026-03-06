package com.example.ordermanagement.application.ports.in;

import com.example.ordermanagement.domain.model.aggregate.Order;
import com.example.ordermanagement.domain.model.entity.OrderItem;

import java.util.List;

/**
 * UC-ORD-01: Create Order
 * Input port for creating a new order with items.
 */
public interface CreateOrderUseCase {

    /**
     * Creates an order with the given items and places it (validates minimum total).
     *
     * @param items the order line items
     * @return the created and placed Order
     */
    Order createOrder(List<OrderItem> items);
}
