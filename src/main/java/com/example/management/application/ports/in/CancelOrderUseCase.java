package com.example.management.application.ports.in;

import com.example.management.domain.model.aggregate.Order;
import com.example.management.domain.model.aggregate.OrderId;

/**
 * UC-ORD-03: Cancel Order. Input port for order cancellation use case.
 */
public interface CancelOrderUseCase {

    /**
     * Cancels the order if allowed (PENDING or PAID status).
     *
     * @param orderId the order to cancel
     * @return the updated Order
     */
    Order cancelOrder(OrderId orderId);
}
