package com.example.management.infrastructure.persistence.adapter;

import com.example.management.domain.port.out.OrderPersistencePort;
import com.example.management.domain.model.Order;
import com.example.management.domain.model.OrderId;
import com.example.management.infrastructure.persistence.entity.OrderEntity;
import com.example.management.infrastructure.persistence.mapper.OrderMapper;
import com.example.management.infrastructure.persistence.repository.OrderJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Infrastructure adapter implementing OrderPersistencePort.
 * Uses OrderJpaRepository and OrderMapper to keep persistence details out of the application layer.
 */
@Component
public class OrderPersistenceJpaAdapter implements OrderPersistencePort {

    private final OrderJpaRepository jpaRepository;
    private final OrderMapper mapper;

    public OrderPersistenceJpaAdapter(OrderJpaRepository jpaRepository, OrderMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Order save(Order order) {
        if (jpaRepository.existsById(order.getId().getValue())) {
            OrderEntity existing = jpaRepository.findByIdWithItems(order.getId().getValue())
                    .orElseThrow(() -> new IllegalStateException("Order not found: " + order.getId().getValue()));
            existing.setStatus(order.getStatus().name());
            OrderEntity saved = jpaRepository.save(existing);
            return mapper.toDomain(saved);
        }
        OrderEntity entity = mapper.toEntity(order);
        OrderEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Order> findById(OrderId orderId) {
        return jpaRepository.findByIdWithItems(orderId.getValue())
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsById(OrderId orderId) {
        return jpaRepository.existsById(orderId.getValue());
    }
}
