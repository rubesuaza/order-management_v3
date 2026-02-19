package com.example.order_management.infrastructure.persistence.mapper;

import com.example.order_management.domain.model.Money;
import com.example.order_management.domain.model.Order;
import com.example.order_management.domain.model.OrderItem;
import com.example.order_management.domain.model.OrderStatus;
import com.example.order_management.infrastructure.persistence.entity.OrderEntity;
import com.example.order_management.infrastructure.persistence.entity.OrderItemEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class OrderMapperTest {

    private OrderMapper orderMapper;
    private UUID customerId;
    private UUID productId1;
    private UUID productId2;

    @BeforeEach
    void setUp() {
        orderMapper = new OrderMapper();
        customerId = UUID.randomUUID();
        productId1 = UUID.randomUUID();
        productId2 = UUID.randomUUID();
    }

    @Test
    void shouldConvertDomainOrderToEntity() {
        // Arrange
        Order order = new Order(customerId, List.of(
                new OrderItem(productId1, 2, new Money(new BigDecimal("10.00"))),
                new OrderItem(productId2, 1, new Money(new BigDecimal("15.00")))
        ));

        // Act
        OrderEntity entity = orderMapper.toEntity(order);

        // Assert
        assertThat(entity.getId()).isEqualTo(order.getId());
        assertThat(entity.getCustomerId()).isEqualTo(customerId);
        assertThat(entity.getStatus()).isEqualTo(OrderStatus.PENDING.name());
        assertThat(entity.getTotalAmount()).isEqualByComparingTo(new BigDecimal("35.00"));
        assertThat(entity.getCurrency()).isEqualTo("USD");
        assertThat(entity.getCreatedAt()).isEqualTo(order.getCreatedAt());
        assertThat(entity.getItems()).hasSize(2);
        
        OrderItemEntity item1 = entity.getItems().get(0);
        assertThat(item1.getProductId()).isEqualTo(productId1);
        assertThat(item1.getQuantity()).isEqualTo(2);
        assertThat(item1.getUnitPrice()).isEqualByComparingTo(new BigDecimal("10.00"));
        assertThat(item1.getOrder()).isEqualTo(entity);
    }

    @Test
    void shouldConvertEntityToDomainOrder() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();
        
        OrderEntity entity = new OrderEntity(
                orderId,
                customerId,
                OrderStatus.PAID.name(),
                new BigDecimal("35.00"),
                "USD",
                createdAt
        );

        OrderItemEntity itemEntity1 = new OrderItemEntity(productId1, 2, new BigDecimal("10.00"));
        OrderItemEntity itemEntity2 = new OrderItemEntity(productId2, 1, new BigDecimal("15.00"));
        itemEntity1.setOrder(entity);
        itemEntity2.setOrder(entity);
        entity.setItems(List.of(itemEntity1, itemEntity2));

        // Act
        Order order = orderMapper.toDomain(entity);

        // Assert
        assertThat(order.getId()).isEqualTo(orderId);
        assertThat(order.getCustomerId()).isEqualTo(customerId);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);
        assertThat(order.getTotalAmount().getAmount()).isEqualByComparingTo(new BigDecimal("35.00"));
        assertThat(order.getTotalAmount().getCurrency()).isEqualTo("USD");
        assertThat(order.getCreatedAt()).isEqualTo(createdAt);
        assertThat(order.getItems()).hasSize(2);
        
        OrderItem item1 = order.getItems().get(0);
        assertThat(item1.getProductId()).isEqualTo(productId1);
        assertThat(item1.getQuantity()).isEqualTo(2);
        assertThat(item1.getUnitPrice().getAmount()).isEqualByComparingTo(new BigDecimal("10.00"));
    }

    @Test
    void shouldConvertOrderWithSingleItem() {
        // Arrange
        Order order = new Order(customerId, List.of(
                new OrderItem(productId1, 1, new Money(new BigDecimal("10.00")))
        ));

        // Act
        OrderEntity entity = orderMapper.toEntity(order);
        Order convertedBack = orderMapper.toDomain(entity);

        // Assert
        assertThat(convertedBack.getId()).isEqualTo(order.getId());
        assertThat(convertedBack.getCustomerId()).isEqualTo(customerId);
        assertThat(convertedBack.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(convertedBack.getItems()).hasSize(1);
    }

    @Test
    void shouldPreserveOrderStatusWhenConverting() {
        // Arrange
        Order order = new Order(customerId, List.of(
                new OrderItem(productId1, 1, new Money(new BigDecimal("10.00")))
        ));
        order.markAsPaid();

        // Act
        OrderEntity entity = orderMapper.toEntity(order);
        Order convertedBack = orderMapper.toDomain(entity);

        // Assert
        assertThat(convertedBack.getStatus()).isEqualTo(OrderStatus.PAID);
    }

    @Test
    void shouldPreserveAllOrderStatuses() {
        // Arrange
        Order order = new Order(customerId, List.of(
                new OrderItem(productId1, 1, new Money(new BigDecimal("10.00")))
        ));

        // Test all status transitions
        OrderStatus[] statuses = {OrderStatus.PENDING, OrderStatus.PAID, OrderStatus.SHIPPED, OrderStatus.DELIVERED};
        
        for (OrderStatus status : statuses) {
            // Set status using reflection or methods
            if (status == OrderStatus.PAID) {
                order.markAsPaid();
            } else if (status == OrderStatus.SHIPPED) {
                order.markAsPaid();
                order.markAsShipped();
            } else if (status == OrderStatus.DELIVERED) {
                order.markAsPaid();
                order.markAsShipped();
                order.markAsDelivered();
            }

            // Act
            OrderEntity entity = orderMapper.toEntity(order);
            Order convertedBack = orderMapper.toDomain(entity);

            // Assert
            assertThat(convertedBack.getStatus()).isEqualTo(status);
            
            // Reset for next iteration
            order = new Order(customerId, List.of(
                    new OrderItem(productId1, 1, new Money(new BigDecimal("10.00")))
            ));
        }
    }
}
