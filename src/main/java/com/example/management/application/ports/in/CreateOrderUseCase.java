package com.example.management.application.ports.in;

import com.example.management.domain.model.aggregate.Order;

/**
 * Input port for UC-ORD-01: Create Order.
 * Defines the contract for creating a new order.
 */
public interface CreateOrderUseCase {

    Order createOrder();
}
