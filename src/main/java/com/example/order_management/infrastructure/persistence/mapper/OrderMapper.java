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

        List<OrderItemEntity> itemEntities = order.getItems().stream()
                .map(item -> {
                    OrderItemEntity itemEntity = new OrderItemEntity(
                            item.getProductId(),
                            item.getQuantity(),
                            item.getUnitPrice().getAmount()
                    );
                    itemEntity.setOrder(entity);
                    return itemEntity;
                })
                .collect(Collectors.toList());

        entity.setItems(itemEntities);
        return entity;
    }

    /**
     * Converts a JPA OrderEntity to a Domain Order.
     */
    public Order toDomain(OrderEntity entity) {
        List<OrderItem> domainItems = entity.getItems().stream()
                .map(item -> new OrderItem(
                        item.getProductId(),
                        item.getQuantity(),
                        new Money(item.getUnitPrice(), entity.getCurrency())
                ))
                .collect(Collectors.toList());

        Order order = new Order(entity.getCustomerId(), domainItems);
        
        // Set the ID using reflection or create a package-private setter
        // Since Order's ID is final, we need to reconstruct it properly
        // For now, we'll use a workaround by creating a new Order and then updating its status
        // Actually, we need to check if Order has a way to set ID after construction
        
        // Better approach: Create Order with a constructor that accepts ID, or use reflection
        // Let's check Order class structure - it has final id field set in constructor
        // We'll need to modify the approach or use reflection
        
        // For now, let's create a method that reconstructs the order properly
        return reconstructOrder(entity, domainItems);
    }

    /**
     * Reconstructs a Domain Order from an entity, preserving the ID.
     * Uses reflection to set the final id field.
     */
    private Order reconstructOrder(OrderEntity entity, List<OrderItem> items) {
        try {
            // Create order with items
            Order order = new Order(entity.getCustomerId(), items);
            
            // Use reflection to set the ID
            java.lang.reflect.Field idField = Order.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(order, entity.getId());
            
            // Set status
            java.lang.reflect.Field statusField = Order.class.getDeclaredField("status");
            statusField.setAccessible(true);
            statusField.set(order, OrderStatus.valueOf(entity.getStatus()));
            
            // Set createdAt
            java.lang.reflect.Field createdAtField = Order.class.getDeclaredField("createdAt");
            createdAtField.setAccessible(true);
            createdAtField.set(order, entity.getCreatedAt());
            
            // Set totalAmount
            java.lang.reflect.Field totalAmountField = Order.class.getDeclaredField("totalAmount");
            totalAmountField.setAccessible(true);
            totalAmountField.set(order, new Money(entity.getTotalAmount(), entity.getCurrency()));
            
            return order;
        } catch (Exception e) {
            throw new RuntimeException("Failed to reconstruct Order from entity", e);
        }
    }
}
