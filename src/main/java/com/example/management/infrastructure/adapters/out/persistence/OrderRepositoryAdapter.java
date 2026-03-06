package com.example.management.infrastructure.adapters.out.persistence;

import com.example.management.application.ports.out.OrderRepository;
import com.example.management.domain.model.aggregate.Order;
import com.example.management.domain.model.entity.OrderItem;
import com.example.management.domain.model.valueobject.Money;
import com.example.management.domain.model.valueobject.OrderId;
import com.example.management.domain.model.valueobject.OrderStatus;
import com.example.management.infrastructure.persistence.entity.OrderEntity;
import com.example.management.infrastructure.persistence.entity.OrderItemEntity;
import com.example.management.infrastructure.persistence.repository.OrderJpaRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

/**
 * Infrastructure adapter implementing OrderRepository port.
 * Translates between domain Order aggregate and JPA persistence model.
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

    @Override
    public boolean existsById(OrderId orderId) {
        return jpaRepository.existsById(orderId.getValue());
    }

    private OrderEntity toEntity(Order order) {
        OrderEntity entity = new OrderEntity(
                order.getId().getValue(),
                toEntityStatus(order.getStatus()),
                order.getTotalAmount().getAmount(),
                order.getTotalAmount().getCurrency()
        );
        for (OrderItem item : order.getItems()) {
            OrderItemEntity itemEntity = new OrderItemEntity(
                    entity,
                    item.getItemId(),
                    item.getProductId(),
                    item.getQuantity(),
                    item.getUnitPrice().getAmount(),
                    item.getUnitPrice().getCurrency()
            );
            entity.getItems().add(itemEntity);
        }
        return entity;
    }

    private Order toDomain(OrderEntity entity) {
        Order order = new Order(OrderId.of(entity.getId()));
        for (OrderItemEntity itemEntity : entity.getItems()) {
            Money unitPrice = Money.of(itemEntity.getUnitPriceAmount(), itemEntity.getUnitPriceCurrency());
            order.addItem(itemEntity.getProductId(), itemEntity.getQuantity(), unitPrice);
        }
        setDomainStatus(order, entity.getStatus());
        return order;
    }

    private void setDomainStatus(Order order, OrderEntity.OrderStatusEntity entityStatus) {
        switch (entityStatus) {
            case PAID -> order.pay();
            case SHIPPED -> {
                order.pay();
                order.ship();
            }
            case CANCELLED -> order.cancel();
            default -> { /* PENDING - no action */ }
        }
    }

    private OrderEntity.OrderStatusEntity toEntityStatus(OrderStatus status) {
        return OrderEntity.OrderStatusEntity.valueOf(status.name());
    }

    private OrderStatus toDomainStatus(OrderEntity.OrderStatusEntity status) {
        return OrderStatus.valueOf(status.name());
    }
}
