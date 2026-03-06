package com.example.ordermanagement.domain.model.aggregate;

import com.example.ordermanagement.domain.exception.CurrencyMismatchException;
import com.example.ordermanagement.domain.exception.InvalidOrderStateException;
import com.example.ordermanagement.domain.model.entity.OrderItem;
import com.example.ordermanagement.domain.model.valueobject.Money;
import com.example.ordermanagement.domain.model.valueobject.OrderId;
import com.example.ordermanagement.domain.model.valueobject.OrderStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    private static final Money TEN_USD = new Money(new BigDecimal("10.00"), "USD");
    private static final Money FIVE_USD = new Money(new BigDecimal("5.00"), "USD");

    @Test
    void shouldCreateOrderWithEmptyItems() {
        Order order = Order.create(OrderId.generate());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertTrue(order.getItems().isEmpty());
        assertEquals(new Money(BigDecimal.ZERO, "USD"), order.getTotalAmount());
    }

    @Test
    void shouldRecalculateTotalWhenItemAdded() {
        Order order = Order.create(OrderId.generate());
        order.addItem(new OrderItem(UUID.randomUUID(), "prod-1", 2, FIVE_USD));
        assertEquals(new Money(new BigDecimal("10.00"), "USD"), order.getTotalAmount());
        assertEquals(1, order.getItems().size());
    }

    @Test
    void shouldRecalculateTotalWhenMultipleItemsAdded() {
        Order order = Order.create(OrderId.generate());
        order.addItem(new OrderItem(UUID.randomUUID(), "prod-1", 2, FIVE_USD));
        order.addItem(new OrderItem(UUID.randomUUID(), "prod-2", 1, TEN_USD));
        assertEquals(new Money(new BigDecimal("20.00"), "USD"), order.getTotalAmount());
    }

    @Test
    void shouldRecalculateTotalWhenItemRemoved() {
        Order order = Order.create(OrderId.generate());
        OrderItem item1 = new OrderItem(UUID.randomUUID(), "prod-1", 2, FIVE_USD);
        OrderItem item2 = new OrderItem(UUID.randomUUID(), "prod-2", 1, TEN_USD);
        order.addItem(item1);
        order.addItem(item2);
        order.removeItem(item1.getId());
        assertEquals(new Money(new BigDecimal("10.00"), "USD"), order.getTotalAmount());
    }

    @Test
    void shouldPlaceOrderWhenTotalAmountAtLeast10USD() {
        Order order = Order.create(OrderId.generate());
        order.addItem(new OrderItem(UUID.randomUUID(), "prod-1", 2, FIVE_USD));
        order.place();
        assertEquals(OrderStatus.PENDING, order.getStatus());
    }

    @Test
    void shouldThrowWhenPlacingOrderWithTotalLessThan10USD() {
        Order order = Order.create(OrderId.generate());
        order.addItem(new OrderItem(UUID.randomUUID(), "prod-1", 1, FIVE_USD));
        assertThrows(InvalidOrderStateException.class, order::place);
    }

    @Test
    void shouldCancelWhenStatusIsPending() {
        Order order = Order.create(OrderId.generate());
        order.addItem(new OrderItem(UUID.randomUUID(), "prod-1", 2, FIVE_USD));
        order.place();
        order.cancel();
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
    }

    @Test
    void shouldCancelWhenStatusIsPaid() {
        Order order = Order.create(OrderId.generate());
        order.addItem(new OrderItem(UUID.randomUUID(), "prod-1", 2, FIVE_USD));
        order.place();
        order.pay();
        order.cancel();
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
    }

    @Test
    void shouldThrowWhenCancellingShippedOrder() {
        Order order = Order.create(OrderId.generate());
        order.addItem(new OrderItem(UUID.randomUUID(), "prod-1", 2, FIVE_USD));
        order.place();
        order.pay();
        order.ship();
        assertThrows(InvalidOrderStateException.class, order::cancel);
    }

    @Test
    void shouldShipWhenStatusIsPaid() {
        Order order = Order.create(OrderId.generate());
        order.addItem(new OrderItem(UUID.randomUUID(), "prod-1", 2, FIVE_USD));
        order.place();
        order.pay();
        order.ship();
        assertEquals(OrderStatus.SHIPPED, order.getStatus());
    }

    @Test
    void shouldThrowWhenShippingNonPaidOrder() {
        Order order = Order.create(OrderId.generate());
        order.addItem(new OrderItem(UUID.randomUUID(), "prod-1", 2, FIVE_USD));
        order.place();
        assertThrows(InvalidOrderStateException.class, order::ship);
    }

    @Test
    void shouldThrowWhenAddingItemWithDifferentCurrency() {
        Order order = Order.create(OrderId.generate());
        Money eur = new Money(new BigDecimal("5.00"), "EUR");
        assertThrows(CurrencyMismatchException.class, () ->
                order.addItem(new OrderItem(UUID.randomUUID(), "prod-1", 1, eur)));
    }

    @Test
    void shouldThrowWhenCancellingAlreadyCancelledOrder() {
        Order order = Order.create(OrderId.generate());
        order.addItem(new OrderItem(UUID.randomUUID(), "prod-1", 2, FIVE_USD));
        order.place();
        order.cancel();
        assertThrows(InvalidOrderStateException.class, order::cancel);
    }

    @Test
    void shouldPayWhenStatusIsPending() {
        Order order = Order.create(OrderId.generate());
        order.addItem(new OrderItem(UUID.randomUUID(), "prod-1", 2, FIVE_USD));
        order.place();
        order.pay();
        assertEquals(OrderStatus.PAID, order.getStatus());
    }

    @Test
    void shouldThrowWhenPayingNonPendingOrder() {
        Order order = Order.create(OrderId.generate());
        order.addItem(new OrderItem(UUID.randomUUID(), "prod-1", 2, FIVE_USD));
        order.place();
        order.pay();
        assertThrows(InvalidOrderStateException.class, order::pay);
    }

    @Test
    void shouldRestoreOrderFromPersistedState() {
        OrderId id = OrderId.generate();
        OrderItem item = new OrderItem(UUID.randomUUID(), "prod-1", 2, FIVE_USD);
        Order restored = Order.restore(id, OrderStatus.PENDING, List.of(item), new Money(new BigDecimal("10.00"), "USD"));
        assertEquals(id, restored.getId());
        assertEquals(OrderStatus.PENDING, restored.getStatus());
        assertEquals(1, restored.getItems().size());
        assertEquals(new Money(new BigDecimal("10.00"), "USD"), restored.getTotalAmount());
    }
}
