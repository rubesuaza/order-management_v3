package com.example.ordermanagement.application.ports.in;

import com.example.ordermanagement.domain.model.valueobject.OrderId;

import java.util.Optional;

/**
 * UC-ORD-02: Pay Order
 * Input port for paying an existing order.
 */
public interface PayOrderUseCase {

    /**
     * Marks the order as paid.
     *
     * @param orderId the order identifier
     * @return the updated Order if found and paid successfully, empty otherwise
     */
    Optional<OrderResult> payOrder(OrderId orderId);
}
