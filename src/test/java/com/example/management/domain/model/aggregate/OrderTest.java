package com.example.management.domain.model.aggregate;

import com.example.management.domain.exception.InvalidOrderStateException;
import com.example.management.domain.model.valueobject.Money;
import com.example.management.domain.model.valueobject.OrderId;
import com.example.management.domain.model.valueobject.OrderStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    @Test
    void addItem_recalculatesTotalAmount() {
        Order order = new Order(OrderId.generate());
        order.addItem("PROD-001", 2, Money.usd(BigDecimal.valueOf(5.00)));
        order.addItem("PROD-002", 1, Money.usd(BigDecimal.valueOf(10.00)));
        assertEquals(0, new BigDecimal("20.00").compareTo(order.getTotalAmount().getAmount()));
        assertEquals(2, order.getItems().size());
    }

    @Test
    void removeItem_recalculatesTotalAmount() {
        Order order = new Order(OrderId.generate());
        order.addItem("PROD-001", 2, Money.usd(BigDecimal.valueOf(5.00)));
        order.addItem("PROD-002", 1, Money.usd(BigDecimal.valueOf(10.00)));
        order.removeItem(1);
        assertEquals(0, new BigDecimal("10.00").compareTo(order.getTotalAmount().getAmount()));
        assertEquals(1, order.getItems().size());
    }

    @Test
    void place_totalBelowMinimum_throwsInvalidOrderStateException() {
        Order order = new Order(OrderId.generate());
        order.addItem("PROD-001", 1, Money.usd(BigDecimal.valueOf(5.00)));
        assertThrows(InvalidOrderStateException.class, order::place);
    }

    @Test
    void place_totalAtOrAboveMinimum_succeeds() {
        Order order = new Order(OrderId.generate());
        order.addItem("PROD-001", 1, Money.usd(BigDecimal.valueOf(10.00)));
        assertDoesNotThrow(order::place);
    }

    @Test
    void pay_fromPending_succeeds() {
        Order order = new Order(OrderId.generate());
        order.addItem("PROD-001", 1, Money.usd(BigDecimal.valueOf(10.00)));
        order.place();
        order.pay();
        assertEquals(OrderStatus.PAID, order.getStatus());
    }

    @Test
    void pay_fromNonPending_throwsInvalidOrderStateException() {
        Order order = new Order(OrderId.generate());
        order.addItem("PROD-001", 1, Money.usd(BigDecimal.valueOf(10.00)));
        order.place();
        order.pay();
        assertThrows(InvalidOrderStateException.class, order::pay);
    }

    @Test
    void cancel_fromPending_succeeds() {
        Order order = new Order(OrderId.generate());
        order.addItem("PROD-001", 1, Money.usd(BigDecimal.valueOf(10.00)));
        order.cancel();
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
    }

    @Test
    void cancel_fromPaid_succeeds() {
        Order order = new Order(OrderId.generate());
        order.addItem("PROD-001", 1, Money.usd(BigDecimal.valueOf(10.00)));
        order.place();
        order.pay();
        order.cancel();
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
    }

    @Test
    void cancel_fromShipped_throwsInvalidOrderStateException() {
        Order order = new Order(OrderId.generate());
        order.addItem("PROD-001", 1, Money.usd(BigDecimal.valueOf(10.00)));
        order.place();
        order.pay();
        order.ship();
        assertThrows(InvalidOrderStateException.class, order::cancel);
    }

    @Test
    void ship_fromPaid_succeeds() {
        Order order = new Order(OrderId.generate());
        order.addItem("PROD-001", 1, Money.usd(BigDecimal.valueOf(10.00)));
        order.place();
        order.pay();
        order.ship();
        assertEquals(OrderStatus.SHIPPED, order.getStatus());
    }

    @Test
    void ship_fromPending_throwsInvalidOrderStateException() {
        Order order = new Order(OrderId.generate());
        order.addItem("PROD-001", 1, Money.usd(BigDecimal.valueOf(10.00)));
        assertThrows(InvalidOrderStateException.class, order::ship);
    }

    @Test
    void addItem_whenNotPending_throwsInvalidOrderStateException() {
        Order order = new Order(OrderId.generate());
        order.addItem("PROD-001", 1, Money.usd(BigDecimal.valueOf(10.00)));
        order.place();
        order.pay();
        assertThrows(InvalidOrderStateException.class,
                () -> order.addItem("PROD-002", 1, Money.usd(BigDecimal.ONE)));
    }
}
