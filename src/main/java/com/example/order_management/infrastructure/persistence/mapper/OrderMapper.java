package com.example.order_management.infrastructure.persistence.mapper;

import com.example.order_management.domain.model.Money;
import com.example.order_management.domain.model.Order;
import com.example.order_management.domain.model.OrderItem;
import com.example.order_management.domain.model.OrderStatus;
import com.example.order_management.infrastructure.persistence.entity.OrderEntity;
import com.example.order_management.infrastructure.persistence.entity.OrderItemEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for converting between Domain Order entities and JPA OrderEntity.
 */
@Component
public class OrderMapper {

    /**
     * Converts a Domain Order to a JPA OrderEntity.
     */
    public OrderEntity toEntity(Order order) {
        OrderEntity entity = new OrderEntity(
                order.getId(),
                order.getCustomerId(),
                order.getStatus().name(),
                order.getTotalAmount().getAmount(),
                order.getTotalAmount().getCurrency(),
                order.getCreatedAt()
        );

        order.getItems().stream()
                .map(item -> new OrderItemEntity(
                        item.getProductId(),
                        item.getQuantity(),
                        item.getUnitPrice().getAmount()
                ))
                .forEach(entity::addItem);
        
        return entity;
    }

    /**
     * Converts a JPA OrderEntity to a Domain Order.
     * Uses the public constructor that accepts all necessary fields for reconstruction.
     */
    public Order toDomain(OrderEntity entity) {
        List<OrderItem> domainItems = entity.getItems().stream()
                .map(item -> new OrderItem(
                        item.getProductId(),
                        item.getQuantity(),
                        new Money(item.getUnitPrice(), entity.getCurrency())
                ))
                .collect(Collectors.toList());

        Money totalAmount = new Money(entity.getTotalAmount(), entity.getCurrency());
        OrderStatus status = OrderStatus.valueOf(entity.getStatus());
        
        return new Order(
                entity.getId(),
                entity.getCustomerId(),
                domainItems,
                status,
                entity.getCreatedAt(),
                totalAmount
        );
    }
}
