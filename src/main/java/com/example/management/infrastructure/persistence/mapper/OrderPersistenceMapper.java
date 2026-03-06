package com.example.management.infrastructure.persistence.mapper;

import com.example.management.domain.model.aggregate.Order;
import com.example.management.domain.model.aggregate.OrderId;
import com.example.management.domain.model.entity.OrderItem;
import com.example.management.domain.model.valueobject.Money;
import com.example.management.domain.model.valueobject.OrderStatus;
import com.example.management.infrastructure.persistence.entity.OrderEntity;
import com.example.management.infrastructure.persistence.entity.OrderItemEntity;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Mapper between domain Order aggregate and JPA persistence entities.
 * Keeps adapter focused on orchestration and mapper on conversion (Repository pattern).
 */
@Component
public class OrderPersistenceMapper {

    public OrderEntity toEntity(Order order) {
        OrderEntity entity = new OrderEntity(
                order.getId().getValue(),
                toEntityStatus(order.getStatus()),
                order.getTotalAmount().getAmount(),
                order.getTotalAmount().getCurrency()
        );
        List<OrderItemEntity> itemEntities = order.getItems().stream()
                .map(item -> toItemEntity(item, entity))
                .toList();
        entity.setItems(itemEntities);
        return entity;
    }

    public Order toDomain(OrderEntity entity) {
        List<OrderItem> items = entity.getItems().stream()
                .map(this::toDomainItem)
                .toList();
        return new Order(
                new OrderId(entity.getId()),
                toDomainStatus(entity.getStatus()),
                items,
                Money.of(entity.getTotalAmount(), entity.getTotalCurrency())
        );
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
