package com.example.ordermanagement.application.ports.in;

import com.example.ordermanagement.domain.model.valueobject.OrderId;

import java.util.Optional;

/**
 * UC-ORD-03: Cancel Order
 * Input port for cancelling an existing order.
 */
public interface CancelOrderUseCase {

    /**
     * Cancels the order if allowed (PENDING or PAID status).
     *
     * @param orderId the order identifier
     * @return the result if found and cancelled successfully, empty otherwise
     */
    Optional<OrderResult> cancelOrder(OrderId orderId);
}
