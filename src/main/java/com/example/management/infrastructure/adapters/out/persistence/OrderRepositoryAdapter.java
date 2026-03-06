package com.example.management.infrastructure.adapters.out.persistence;

import com.example.management.application.ports.out.OrderRepository;
import com.example.management.domain.model.aggregate.Order;
import com.example.management.domain.model.valueobject.OrderId;
import com.example.management.infrastructure.persistence.entity.OrderEntity;
import com.example.management.infrastructure.persistence.repository.OrderJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Infrastructure adapter implementing OrderRepository port.
 * Delegates mapping to OrderPersistenceMapper per Repository pattern.
 */
@Component
public class OrderRepositoryAdapter implements OrderRepository {

    private final OrderJpaRepository jpaRepository;
    private final OrderPersistenceMapper mapper;

    public OrderRepositoryAdapter(OrderJpaRepository jpaRepository, OrderPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Order save(Order order) {
        OrderEntity entity = mapper.toEntity(order);
        OrderEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Order> findById(OrderId orderId) {
        return jpaRepository.findById(orderId.getValue())
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsById(OrderId orderId) {
        return jpaRepository.existsById(orderId.getValue());
    }
}
