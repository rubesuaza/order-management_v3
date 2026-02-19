package com.example.order_management.domain.model;

import com.example.order_management.domain.exception.InvalidOrderStateException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderTest {

    @Test
    void shouldCreateOrderWithAtLeastOneItem() {
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        Money unitPrice = new Money(new BigDecimal("25.50"));
        OrderItem item = new OrderItem(productId, 2, unitPrice);
        
        Order order = new Order(customerId, List.of(item));
        
        assertThat(order.getCustomerId()).isEqualTo(customerId);
        assertThat(order.getItems()).hasSize(1);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(order.getTotalAmount().getAmount()).isEqualByComparingTo(new BigDecimal("51.00"));
    }

    @Test
    void shouldThrowExceptionWhenCreatingOrderWithNoItems() {
        UUID customerId = UUID.randomUUID();
        
        assertThatThrownBy(() -> new Order(customerId, new ArrayList<>()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Order must have at least one item");
    }

    @Test
    void shouldCalculateTotalAmountCorrectly() {
        UUID customerId = UUID.randomUUID();
        UUID productId1 = UUID.randomUUID();
        UUID productId2 = UUID.randomUUID();
        
        OrderItem item1 = new OrderItem(productId1, 2, new Money(new BigDecimal("10.00")));
        OrderItem item2 = new OrderItem(productId2, 3, new Money(new BigDecimal("15.00")));
        
        Order order = new Order(customerId, List.of(item1, item2));
        
        // Total: (2 * 10.00) + (3 * 15.00) = 20.00 + 45.00 = 65.00
        assertThat(order.getTotalAmount().getAmount()).isEqualByComparingTo(new BigDecimal("65.00"));
    }

    @Test
    void shouldMarkAsPaidWhenTotalIsAtLeastTenDollars() {
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        OrderItem item = new OrderItem(productId, 1, new Money(new BigDecimal("10.00")));
        Order order = new Order(customerId, List.of(item));
        
        order.markAsPaid();
        
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);
    }

    @Test
    void shouldThrowExceptionWhenMarkingAsPaidWithTotalLessThanTenDollars() {
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        OrderItem item = new OrderItem(productId, 1, new Money(new BigDecimal("9.99")));
        Order order = new Order(customerId, List.of(item));
        
        assertThatThrownBy(() -> order.markAsPaid())
                .isInstanceOf(InvalidOrderStateException.class)
                .hasMessageContaining("Minimum order value");
    }

    @Test
    void shouldCancelOrderWhenStatusIsPending() {
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        OrderItem item = new OrderItem(productId, 1, new Money(new BigDecimal("10.00")));
        Order order = new Order(customerId, List.of(item));
        
        order.cancel();
        
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    void shouldCancelOrderWhenStatusIsPaid() {
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        OrderItem item = new OrderItem(productId, 1, new Money(new BigDecimal("10.00")));
        Order order = new Order(customerId, List.of(item));
        order.markAsPaid();
        
        order.cancel();
        
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    void shouldThrowExceptionWhenCancellingShippedOrder() {
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        OrderItem item = new OrderItem(productId, 1, new Money(new BigDecimal("10.00")));
        Order order = new Order(customerId, List.of(item));
        order.markAsPaid();
        order.markAsShipped();
        
        assertThatThrownBy(() -> order.cancel())
                .isInstanceOf(InvalidOrderStateException.class)
                .hasMessageContaining("cannot be cancelled");
    }

    @Test
    void shouldMarkAsShippedWhenStatusIsPaid() {
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        OrderItem item = new OrderItem(productId, 1, new Money(new BigDecimal("10.00")));
        Order order = new Order(customerId, List.of(item));
        order.markAsPaid();
        
        order.markAsShipped();
        
        assertThat(order.getStatus()).isEqualTo(OrderStatus.SHIPPED);
    }

    @Test
    void shouldThrowExceptionWhenShippingOrderThatIsNotPaid() {
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        OrderItem item = new OrderItem(productId, 1, new Money(new BigDecimal("10.00")));
        Order order = new Order(customerId, List.of(item));
        
        assertThatThrownBy(() -> order.markAsShipped())
                .isInstanceOf(InvalidOrderStateException.class)
                .hasMessageContaining("can only be shipped");
    }

    @Test
    void shouldMarkAsDeliveredWhenStatusIsShipped() {
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        OrderItem item = new OrderItem(productId, 1, new Money(new BigDecimal("10.00")));
        Order order = new Order(customerId, List.of(item));
        order.markAsPaid();
        order.markAsShipped();
        
        order.markAsDelivered();
        
        assertThat(order.getStatus()).isEqualTo(OrderStatus.DELIVERED);
    }

    @Test
    void shouldHaveCreatedAtTimestamp() {
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        OrderItem item = new OrderItem(productId, 1, new Money(new BigDecimal("10.00")));
        Order order = new Order(customerId, List.of(item));
        
        assertThat(order.getCreatedAt()).isNotNull();
        assertThat(order.getCreatedAt()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    void shouldGenerateOrderId() {
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        OrderItem item = new OrderItem(productId, 1, new Money(new BigDecimal("10.00")));
        Order order = new Order(customerId, List.of(item));
        
        assertThat(order.getId()).isNotNull();
    }
}
