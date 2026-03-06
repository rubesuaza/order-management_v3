package com.example.ordermanagement.infrastructure.adapter;

import com.example.ordermanagement.application.ports.out.OrderRepository;
import com.example.ordermanagement.domain.model.aggregate.Order;
import com.example.ordermanagement.domain.model.entity.OrderItem;
import com.example.ordermanagement.domain.model.valueobject.Money;
import com.example.ordermanagement.domain.model.valueobject.OrderId;
import com.example.ordermanagement.infrastructure.persistence.entity.OrderEntity;
import com.example.ordermanagement.infrastructure.persistence.entity.OrderItemEntity;
import com.example.ordermanagement.infrastructure.persistence.repository.OrderJpaRepository;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Infrastructure adapter implementing OrderRepository port.
 * Persists Order aggregate using JPA.
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
    public Optional<Order> findById(OrderId orderId) {
        return jpaRepository.findById(orderId.getValue())
                .map(this::toDomain);
    }

    private OrderEntity toEntity(Order order) {
        OrderEntity entity = new OrderEntity(
                order.getId().getValue(),
                order.getStatus(),
                order.getTotalAmount().getAmount(),
                order.getTotalAmount().getCurrency()
        );
        entity.clearItems();
        for (OrderItem item : order.getItems()) {
            OrderItemEntity itemEntity = new OrderItemEntity(
                    item.getId(),
                    item.getProductId(),
                    item.getQuantity(),
                    item.getUnitPrice().getAmount(),
                    item.getUnitPrice().getCurrency(),
                    entity
            );
            entity.addItem(itemEntity);
        }
        return entity;
    }

    private Order toDomain(OrderEntity entity) {
        List<OrderItem> items = entity.getItems().stream()
                .map(itemEntity -> {
                    Money unitPrice = new Money(itemEntity.getUnitPriceAmount(), itemEntity.getUnitPriceCurrency());
                    return new OrderItem(
                            itemEntity.getId(),
                            itemEntity.getProductId(),
                            itemEntity.getQuantity(),
                            unitPrice
                    );
                })
                .collect(Collectors.toList());
        Money totalAmount = new Money(entity.getTotalAmount(), entity.getTotalCurrency());
        return Order.restore(
                new OrderId(entity.getId()),
                entity.getStatus(),
                items,
                totalAmount
        );
    }
}
