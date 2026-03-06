package com.example.management.application.ports.in;

import com.example.management.domain.model.valueobject.OrderId;

/**
 * Input port for UC-ORD-02: Pay Order.
 * Defines the contract for paying an order.
 */
public interface PayOrderUseCase {

    void payOrder(OrderId orderId);
}
