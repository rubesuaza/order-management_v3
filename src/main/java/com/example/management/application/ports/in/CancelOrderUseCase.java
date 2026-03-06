package com.example.management.application.ports.in;

import com.example.management.domain.model.valueobject.OrderId;

/**
 * Input port for UC-ORD-03: Cancel Order.
 * Defines the contract for cancelling an order.
 */
public interface CancelOrderUseCase {

    void cancelOrder(OrderId orderId);
}
