package com.example.management.infrastructure.persistence.mapper;

import com.example.management.domain.OrderStatus;
import com.example.management.domain.model.Order;
import com.example.management.domain.model.OrderItem;
import com.example.management.domain.valueobject.Money;
import com.example.management.infrastructure.persistence.entity.OrderEntity;
import com.example.management.infrastructure.persistence.entity.OrderItemEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Maps between domain Order/OrderItem and persistence OrderEntity/OrderItemEntity.
 */
@Component
public class OrderMapper {

    public OrderEntity toEntity(Order order) {
        OrderEntity entity = new OrderEntity(
                order.getId().getValue(),
                order.getCustomerId(),
                order.getStatus().name(),
                order.getTotalAmount().getAmount(),
                order.getTotalAmount().getCurrency(),
                order.getCreatedAt()
        );
        List<OrderItemEntity> itemEntities = order.getItems().stream()
                .map(item -> toItemEntity(item, entity))
                .collect(Collectors.toList());
        entity.setItems(itemEntities);
        return entity;
    }

    private OrderItemEntity toItemEntity(OrderItem item, OrderEntity order) {
        OrderItemEntity e = new OrderItemEntity(
                UUID.randomUUID(),
                item.getProductId(),
                item.getQuantity(),
                item.getUnitPrice().getAmount()
        );
        e.setOrder(order);
        return e;
    }

    public Order toDomain(OrderEntity entity) {
        List<OrderItem> items = entity.getItems().stream()
                .map(this::toDomainItem)
                .collect(Collectors.toList());
        return Order.reconstitute(
                entity.getId(),
                entity.getCustomerId(),
                entity.getCreatedAt(),
                items,
                OrderStatus.valueOf(entity.getStatus())
        );
    }

    private OrderItem toDomainItem(OrderItemEntity e) {
        return new OrderItem(
                e.getProductId(),
                e.getQuantity(),
                new Money(e.getUnitPrice(), null)
        );
    }
}
