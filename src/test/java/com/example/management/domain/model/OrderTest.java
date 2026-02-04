package com.example.management.domain.model;

import com.example.management.domain.exception.InvalidOrderStateException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderTest {

    private static final UUID ORDER_ID = UUID.randomUUID();
    private static final UUID CUSTOMER_ID = UUID.randomUUID();
    private static final UUID PRODUCT_ID = UUID.randomUUID();
    private static final Money TEN_USD = new Money(new BigDecimal("10.00"));
    private static final Money FIVE_USD = new Money(new BigDecimal("5.00"));

    @Test
    void shouldCreateOrderWithAtLeastOneItem() {
        OrderItem item = new OrderItem(PRODUCT_ID, 2, TEN_USD);
        Order order = new Order(ORDER_ID, CUSTOMER_ID, List.of(item), LocalDateTime.now());
        assertThat(order.getId()).isEqualTo(ORDER_ID);
        assertThat(order.getCustomerId()).isEqualTo(CUSTOMER_ID);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(order.getItems()).hasSize(1);
        assertThat(order.getTotalAmount().getAmount()).isEqualByComparingTo(new BigDecimal("20.00"));
    }

    @Test
    void totalAmount_shouldBeSumOfLineTotals() {
        OrderItem item1 = new OrderItem(PRODUCT_ID, 2, TEN_USD);
        OrderItem item2 = new OrderItem(UUID.randomUUID(), 1, FIVE_USD);
        Order order = new Order(ORDER_ID, CUSTOMER_ID, List.of(item1, item2), LocalDateTime.now());
        assertThat(order.getTotalAmount().getAmount()).isEqualByComparingTo(new BigDecimal("25.00"));
    }

    @Test
    void shouldRejectOrderWithNoItems() {
        assertThatThrownBy(() -> new Order(ORDER_ID, CUSTOMER_ID, List.of(), LocalDateTime.now()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("at least one");
    }

    @Test
    void markAsPaid_shouldSucceedWhenTotalAtLeast10USD() {
        OrderItem item = new OrderItem(PRODUCT_ID, 1, TEN_USD);
        Order order = new Order(ORDER_ID, CUSTOMER_ID, List.of(item), LocalDateTime.now());
        order.markAsPaid();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);
    }

    @Test
    void markAsPaid_shouldThrowWhenTotalLessThan10USD() {
        OrderItem item = new OrderItem(PRODUCT_ID, 1, FIVE_USD);
        Order order = new Order(ORDER_ID, CUSTOMER_ID, List.of(item), LocalDateTime.now());
        assertThatThrownBy(order::markAsPaid)
                .isInstanceOf(InvalidOrderStateException.class)
                .hasMessageContaining("10");
    }

    @Test
    void ship_shouldSucceedWhenPaid() {
        OrderItem item = new OrderItem(PRODUCT_ID, 2, TEN_USD);
        Order order = new Order(ORDER_ID, CUSTOMER_ID, List.of(item), LocalDateTime.now());
        order.markAsPaid();
        order.ship();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.SHIPPED);
    }

    @Test
    void ship_shouldThrowWhenNotPaid() {
        OrderItem item = new OrderItem(PRODUCT_ID, 2, TEN_USD);
        Order order = new Order(ORDER_ID, CUSTOMER_ID, List.of(item), LocalDateTime.now());
        assertThatThrownBy(order::ship)
                .isInstanceOf(InvalidOrderStateException.class)
                .hasMessageContaining("PAID");
    }

    @Test
    void cancel_shouldSucceedWhenPending() {
        OrderItem item = new OrderItem(PRODUCT_ID, 2, TEN_USD);
        Order order = new Order(ORDER_ID, CUSTOMER_ID, List.of(item), LocalDateTime.now());
        order.cancel();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    void cancel_shouldSucceedWhenPaid() {
        OrderItem item = new OrderItem(PRODUCT_ID, 2, TEN_USD);
        Order order = new Order(ORDER_ID, CUSTOMER_ID, List.of(item), LocalDateTime.now());
        order.markAsPaid();
        order.cancel();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    void cancel_shouldThrowWhenShipped() {
        OrderItem item = new OrderItem(PRODUCT_ID, 2, TEN_USD);
        Order order = new Order(ORDER_ID, CUSTOMER_ID, List.of(item), LocalDateTime.now());
        order.markAsPaid();
        order.ship();
        assertThatThrownBy(order::cancel)
                .isInstanceOf(InvalidOrderStateException.class)
                .hasMessageContaining("cancel");
    }

    @Test
    void deliver_shouldSucceedWhenShipped() {
        OrderItem item = new OrderItem(PRODUCT_ID, 2, TEN_USD);
        Order order = new Order(ORDER_ID, CUSTOMER_ID, List.of(item), LocalDateTime.now());
        order.markAsPaid();
        order.ship();
        order.deliver();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.DELIVERED);
    }

    @Test
    void getCreatedAt_shouldReturnCreationTime() {
        LocalDateTime now = LocalDateTime.now();
        OrderItem item = new OrderItem(PRODUCT_ID, 1, TEN_USD);
        Order order = new Order(ORDER_ID, CUSTOMER_ID, List.of(item), now);
        assertThat(order.getCreatedAt()).isEqualTo(now);
    }
}
