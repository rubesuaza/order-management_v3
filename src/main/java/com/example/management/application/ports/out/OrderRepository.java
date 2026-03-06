package com.example.management.application.ports.out;

import com.example.management.domain.model.aggregate.Order;
import com.example.management.domain.model.valueobject.OrderId;

import java.util.Optional;

/**
 * Output port for Order persistence.
 * Implemented by infrastructure adapters.
 */
public interface OrderRepository {

    Order save(Order order);

    Optional<Order> findById(OrderId orderId);

    boolean existsById(OrderId orderId);
}
