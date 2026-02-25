package com.example.management.application.port.in;

import com.example.management.domain.model.Order;
import com.example.management.domain.model.OrderId;

/**
 * Input port: process payment for an order (simulated). Transitions status to PAID.
 */
public interface PayOrderUseCase {

    /**
     * @param orderId order to pay
     * @return the order after payment
     * @throws com.example.management.domain.exception.InvalidOrderStateException if not PENDING or total &lt; 10 USD
     */
    Order pay(OrderId orderId);
}
