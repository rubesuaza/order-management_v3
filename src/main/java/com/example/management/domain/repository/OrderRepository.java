package com.example.management.domain.repository;

import com.example.management.domain.model.aggregate.Order;
import com.example.management.domain.model.aggregate.OrderId;

import java.util.Optional;

/**
 * Domain abstraction for Order persistence. Technology-agnostic contract;
 * infrastructure adapters implement this via application ports.
 */
public interface OrderRepository {

    Order save(Order order);

    Optional<Order> findById(OrderId id);

    boolean existsById(OrderId id);
}
