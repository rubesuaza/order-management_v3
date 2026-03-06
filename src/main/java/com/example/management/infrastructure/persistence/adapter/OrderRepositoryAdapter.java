package com.example.management.infrastructure.persistence.adapter;

import com.example.management.application.ports.out.OrderRepository;
import com.example.management.domain.model.aggregate.Order;
import com.example.management.domain.model.aggregate.OrderId;
import com.example.management.domain.model.entity.OrderItem;
import com.example.management.domain.model.valueobject.Money;
import com.example.management.domain.model.valueobject.OrderStatus;
import com.example.management.infrastructure.persistence.entity.OrderEntity;
import com.example.management.infrastructure.persistence.entity.OrderItemEntity;
import com.example.management.infrastructure.persistence.repository.OrderJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Infrastructure adapter implementing OrderRepository port.
 * Maps between domain aggregates and JPA entities.
 */
@Component
public class OrderRepositoryAdapter implements OrderRepository {

    private final OrderJpaRepository jpaRepository;

    public OrderRepositoryAdapter(OrderJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Order save(Order order) {
        OrderEntity entity = toEntity(order);
        OrderEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Order> findById(OrderId id) {
        return jpaRepository.findById(id.getValue())
                .map(this::toDomain);
    }

    @Override
    public boolean existsById(OrderId id) {
        return jpaRepository.existsById(id.getValue());
    }

    private OrderEntity toEntity(Order order) {
        OrderEntity entity = new OrderEntity(
                order.getId().getValue(),
                toEntityStatus(order.getStatus()),
                order.getTotalAmount().getAmount(),
                order.getTotalAmount().getCurrency()
        );
        List<OrderItemEntity> itemEntities = order.getItems().stream()
                .map(item -> toItemEntity(item, entity))
                .collect(Collectors.toList());
        entity.setItems(itemEntities);
        return entity;
    }

    private OrderItemEntity toItemEntity(OrderItem item, OrderEntity order) {
        OrderItemEntity entity = new OrderItemEntity(
                item.getId(),
                item.getProductId(),
                item.getQuantity(),
                item.getUnitPrice().getAmount(),
                item.getUnitPrice().getCurrency()
        );
        entity.setOrder(order);
        return entity;
    }

    private Order toDomain(OrderEntity entity) {
        List<OrderItem> items = entity.getItems().stream()
                .map(this::toDomainItem)
                .collect(Collectors.toList());
        return new Order(
                new OrderId(entity.getId()),
                toDomainStatus(entity.getStatus()),
                items,
                Money.of(entity.getTotalAmount(), entity.getTotalCurrency())
        );
    }

    private OrderItem toDomainItem(OrderItemEntity entity) {
        return new OrderItem(
                entity.getId(),
                entity.getProductId(),
                entity.getQuantity(),
                Money.of(entity.getUnitPrice(), entity.getUnitCurrency())
        );
    }

    private OrderEntity.OrderStatusEntity toEntityStatus(OrderStatus status) {
        return OrderEntity.OrderStatusEntity.valueOf(status.name());
    }

    private OrderStatus toDomainStatus(OrderEntity.OrderStatusEntity status) {
        return OrderStatus.valueOf(status.name());
    }
}
