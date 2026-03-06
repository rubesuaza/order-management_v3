package com.example.management.application.ports.in;

import com.example.management.domain.model.aggregate.Order;
import com.example.management.domain.model.aggregate.OrderId;

/**
 * UC-ORD-02: Pay Order. Input port for order payment use case.
 */
public interface PayOrderUseCase {

    /**
     * Marks the order as paid.
     *
     * @param orderId the order to pay
     * @return the updated Order
     */
    Order payOrder(OrderId orderId);
}
