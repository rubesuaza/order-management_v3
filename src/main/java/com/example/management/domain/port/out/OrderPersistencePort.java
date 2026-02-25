package com.example.management.domain.port.out;

import com.example.management.domain.model.Order;
import com.example.management.domain.model.OrderId;

import java.util.Optional;

/**
 * Output port for order persistence. Implemented by the infrastructure layer.
 * Defined in the domain layer so the domain remains independent and defines its own needs.
 */
public interface OrderPersistencePort {

    Order save(Order order);

    Optional<Order> findById(OrderId orderId);

    boolean existsById(OrderId orderId);
}
