package com.example.management.application.port.out;

import com.example.management.domain.model.Order;
import com.example.management.domain.model.OrderId;

import java.util.Optional;

/**
 * Output port for order persistence. Implemented by the infrastructure layer.
 */
public interface OrderPersistencePort {

    Order save(Order order);

    Optional<Order> findById(OrderId orderId);

    boolean existsById(OrderId orderId);
}
