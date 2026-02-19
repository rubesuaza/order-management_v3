package com.example.order_management.infrastructure.adapters.in.web.mapper;

import com.example.order_management.domain.model.Money;
import com.example.order_management.domain.model.Order;
import com.example.order_management.domain.model.OrderItem;
import com.example.order_management.domain.model.OrderStatus;
import com.example.order_management.infrastructure.adapters.in.web.dto.CreateOrderResponse;
import com.example.order_management.infrastructure.adapters.in.web.dto.OrderResponse;
import com.example.order_management.infrastructure.adapters.in.web.dto.PayOrderResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class OrderDtoMapperTest {

    private OrderDtoMapper orderDtoMapper;
    private UUID customerId;
    private UUID productId1;
    private UUID productId2;
    private UUID orderId;

    @BeforeEach
    void setUp() {
        orderDtoMapper = new OrderDtoMapper();
        customerId = UUID.randomUUID();
        productId1 = UUID.randomUUID();
        productId2 = UUID.randomUUID();
        orderId = UUID.randomUUID();
    }

    @Test
    void shouldConvertOrderToCreateOrderResponse() {
        // Arrange
        Order order = new Order(customerId, List.of(
                new OrderItem(productId1, 2, new Money(new BigDecimal("10.00")))
        ));

        // Act
        CreateOrderResponse response = orderDtoMapper.toCreateResponse(order);

        // Assert
        assertThat(response.orderId()).isEqualTo(order.getId());
        assertThat(response.status()).isEqualTo(OrderStatus.PENDING.name());
        assertThat(response.totalAmount()).isEqualByComparingTo(new BigDecimal("20.00"));
        assertThat(response.createdAt()).isEqualTo(order.getCreatedAt());
    }

    @Test
    void shouldConvertOrderToOrderResponse() {
        // Arrange
        Order order = new Order(customerId, List.of(
                new OrderItem(productId1, 2, new Money(new BigDecimal("10.00"))),
                new OrderItem(productId2, 1, new Money(new BigDecimal("15.00")))
        ));

        // Act
        OrderResponse response = orderDtoMapper.toOrderResponse(order);

        // Assert
        assertThat(response.orderId()).isEqualTo(order.getId());
        assertThat(response.customerId()).isEqualTo(customerId);
        assertThat(response.status()).isEqualTo(OrderStatus.PENDING.name());
        assertThat(response.totalAmount()).isEqualByComparingTo(new BigDecimal("35.00"));
        assertThat(response.currency()).isEqualTo("USD");
        assertThat(response.createdAt()).isEqualTo(order.getCreatedAt());
        assertThat(response.items()).hasSize(2);
        
        OrderResponse.OrderItemResponse item1 = response.items().get(0);
        assertThat(item1.productId()).isEqualTo(productId1);
        assertThat(item1.quantity()).isEqualTo(2);
        assertThat(item1.unitPrice()).isEqualByComparingTo(new BigDecimal("10.00"));
        
        OrderResponse.OrderItemResponse item2 = response.items().get(1);
        assertThat(item2.productId()).isEqualTo(productId2);
        assertThat(item2.quantity()).isEqualTo(1);
        assertThat(item2.unitPrice()).isEqualByComparingTo(new BigDecimal("15.00"));
    }

    @Test
    void shouldConvertOrderToPayOrderResponse() {
        // Arrange
        Order order = new Order(customerId, List.of(
                new OrderItem(productId1, 1, new Money(new BigDecimal("10.00")))
        ));
        order.markAsPaid();

        // Act
        PayOrderResponse response = orderDtoMapper.toPayResponse(order);

        // Assert
        assertThat(response.orderId()).isEqualTo(order.getId());
        assertThat(response.status()).isEqualTo(OrderStatus.PAID.name());
    }

    @Test
    void shouldConvertOrderWithDifferentStatuses() {
        // Arrange
        Order order = new Order(customerId, List.of(
                new OrderItem(productId1, 1, new Money(new BigDecimal("10.00")))
        ));

        // Test PENDING status
        CreateOrderResponse pendingResponse = orderDtoMapper.toCreateResponse(order);
        assertThat(pendingResponse.status()).isEqualTo(OrderStatus.PENDING.name());

        // Test PAID status
        order.markAsPaid();
        PayOrderResponse paidResponse = orderDtoMapper.toPayResponse(order);
        assertThat(paidResponse.status()).isEqualTo(OrderStatus.PAID.name());

        // Test SHIPPED status
        order.markAsShipped();
        OrderResponse shippedResponse = orderDtoMapper.toOrderResponse(order);
        assertThat(shippedResponse.status()).isEqualTo(OrderStatus.SHIPPED.name());
    }

    @Test
    void shouldConvertOrderWithSingleItem() {
        // Arrange
        Order order = new Order(customerId, List.of(
                new OrderItem(productId1, 1, new Money(new BigDecimal("10.00")))
        ));

        // Act
        OrderResponse response = orderDtoMapper.toOrderResponse(order);

        // Assert
        assertThat(response.items()).hasSize(1);
        assertThat(response.items().get(0).productId()).isEqualTo(productId1);
        assertThat(response.items().get(0).quantity()).isEqualTo(1);
    }

    @Test
    void shouldPreserveAllOrderFieldsInOrderResponse() {
        // Arrange
        Order order = new Order(customerId, List.of(
                new OrderItem(productId1, 3, new Money(new BigDecimal("25.50")))
        ));

        // Act
        OrderResponse response = orderDtoMapper.toOrderResponse(order);

        // Assert
        assertThat(response.orderId()).isEqualTo(order.getId());
        assertThat(response.customerId()).isEqualTo(order.getCustomerId());
        assertThat(response.status()).isEqualTo(order.getStatus().name());
        assertThat(response.totalAmount()).isEqualByComparingTo(order.getTotalAmount().getAmount());
        assertThat(response.currency()).isEqualTo(order.getTotalAmount().getCurrency());
        assertThat(response.createdAt()).isEqualTo(order.getCreatedAt());
    }
}
