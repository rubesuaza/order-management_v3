package com.example.management.domain.model.aggregate;

import com.example.management.domain.exception.CurrencyMismatchException;
import com.example.management.domain.exception.InvalidItemException;
import com.example.management.domain.exception.InvalidOrderStateException;
import com.example.management.domain.model.entity.OrderItem;
import com.example.management.domain.model.valueobject.Money;
import com.example.management.domain.model.valueobject.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    private Order order;

    @BeforeEach
    void setUp() {
        order = new Order(OrderId.generate());
    }

    @Test
    void addItem_recalculatesTotalAmount() {
        order.addItem(new OrderItem("prod-1", 2, Money.usd(new BigDecimal("5.00"))));
        assertEquals(new BigDecimal("10.00"), order.getTotalAmount().getAmount());

        order.addItem(new OrderItem("prod-2", 1, Money.usd(new BigDecimal("3.50"))));
        assertEquals(new BigDecimal("13.50"), order.getTotalAmount().getAmount());
    }

    @Test
    void removeItem_recalculatesTotalAmount() {
        OrderItem item1 = new OrderItem("prod-1", 2, Money.usd(new BigDecimal("5.00")));
        OrderItem item2 = new OrderItem("prod-2", 1, Money.usd(new BigDecimal("3.50")));
        order.addItem(item1);
        order.addItem(item2);
        assertEquals(new BigDecimal("13.50"), order.getTotalAmount().getAmount());

        order.removeItem(item1.getId());
        assertEquals(new BigDecimal("3.50"), order.getTotalAmount().getAmount());
    }

    @Test
    void placeWithNoItemsThrowsInvalidOrderStateException() {
        assertThrows(InvalidOrderStateException.class, () -> order.place());
    }

    @Test
    void placeTotalLessThan10ThrowsInvalidOrderStateException() {
        order.addItem(new OrderItem("prod-1", 1, Money.usd(new BigDecimal("5.00"))));
        assertThrows(InvalidOrderStateException.class, () -> order.place());
    }

    @Test
    void placeTotalExactly10Succeeds() {
        order.addItem(new OrderItem("prod-1", 2, Money.usd(new BigDecimal("5.00"))));
        assertDoesNotThrow(() -> order.place());
    }

    @Test
    void placeTotalGreaterThan10Succeeds() {
        order.addItem(new OrderItem("prod-1", 3, Money.usd(new BigDecimal("5.00"))));
        assertDoesNotThrow(() -> order.place());
    }

    @Test
    void cancelWhenPendingSucceeds() {
        order.addItem(new OrderItem("prod-1", 2, Money.usd(new BigDecimal("5.00"))));
        order.cancel();
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
    }

    @Test
    void cancelWhenPaidSucceeds() {
        order.addItem(new OrderItem("prod-1", 2, Money.usd(new BigDecimal("5.00"))));
        order.pay();
        order.cancel();
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
    }

    @Test
    void cancelWhenShippedThrowsInvalidOrderStateException() {
        order.addItem(new OrderItem("prod-1", 2, Money.usd(new BigDecimal("5.00"))));
        order.pay();
        order.ship();
        assertThrows(InvalidOrderStateException.class, () -> order.cancel());
    }

    @Test
    void shipWhenNotPaidThrowsInvalidOrderStateException() {
        order.addItem(new OrderItem("prod-1", 2, Money.usd(new BigDecimal("5.00"))));
        assertThrows(InvalidOrderStateException.class, () -> order.ship());
    }

    @Test
    void shipWhenPaidSucceeds() {
        order.addItem(new OrderItem("prod-1", 2, Money.usd(new BigDecimal("5.00"))));
        order.pay();
        order.ship();
        assertEquals(OrderStatus.SHIPPED, order.getStatus());
    }

    @Test
    void addItem_differentCurrency_throwsCurrencyMismatchException() {
        order.addItem(new OrderItem("prod-1", 1, Money.usd(BigDecimal.ONE)));
        assertThrows(CurrencyMismatchException.class,
                () -> order.addItem(new OrderItem("prod-2", 1, Money.of(BigDecimal.ONE, "EUR"))));
    }

    @Test
    void addItem_whenNotPending_throwsInvalidOrderStateException() {
        order.addItem(new OrderItem("prod-1", 2, Money.usd(new BigDecimal("5.00"))));
        order.pay();
        assertThrows(InvalidOrderStateException.class,
                () -> order.addItem(new OrderItem("prod-2", 1, Money.usd(BigDecimal.ONE))));
    }

    @Test
    void payTransitionsFromPendingToPaid() {
        order.addItem(new OrderItem("prod-1", 2, Money.usd(new BigDecimal("5.00"))));
        order.pay();
        assertEquals(OrderStatus.PAID, order.getStatus());
    }

    @Test
    void payWhenNotPendingThrowsInvalidOrderStateException() {
        order.addItem(new OrderItem("prod-1", 2, Money.usd(new BigDecimal("5.00"))));
        order.pay();
        assertThrows(InvalidOrderStateException.class, () -> order.pay());
    }

    @Test
    void cancelWhenAlreadyCancelledThrowsInvalidOrderStateException() {
        order.addItem(new OrderItem("prod-1", 2, Money.usd(new BigDecimal("5.00"))));
        order.cancel();
        assertThrows(InvalidOrderStateException.class, () -> order.cancel());
    }

    @Test
    void addItem_nullItem_throwsInvalidItemException() {
        assertThrows(InvalidItemException.class, () -> order.addItem(null));
    }

    @Test
    void constructorNullOrderIdThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Order((OrderId) null));
    }

    @Test
    void reconstitutionConstructorRestoresStateCorrectly() {
        OrderId id = OrderId.generate();
        OrderItem item = new OrderItem("prod-1", 2, Money.usd(new BigDecimal("5.00")));
        Order reconstituted = new Order(id, OrderStatus.PENDING, List.of(item), Money.usd(new BigDecimal("10.00")));

        assertEquals(id, reconstituted.getId());
        assertEquals(OrderStatus.PENDING, reconstituted.getStatus());
        assertEquals(1, reconstituted.getItems().size());
        assertEquals(new BigDecimal("10.00"), reconstituted.getTotalAmount().getAmount());
    }

    @Test
    void reconstitutionConstructorEmptyItemsThrowsInvalidOrderStateException() {
        OrderId id = OrderId.generate();
        assertThrows(InvalidOrderStateException.class,
                () -> new Order(id, OrderStatus.PENDING, List.of(), Money.usd(BigDecimal.ZERO)));
    }

    @Test
    void reconstitutionConstructorNullItemsThrowsInvalidOrderStateException() {
        OrderId id = OrderId.generate();
        assertThrows(InvalidOrderStateException.class,
                () -> new Order(id, OrderStatus.PENDING, null, Money.usd(BigDecimal.ZERO)));
    }

    @Test
    void removeItem_whenNotPending_throwsInvalidOrderStateException() {
        OrderItem item = new OrderItem("prod-1", 2, Money.usd(new BigDecimal("5.00")));
        order.addItem(item);
        order.pay();
        assertThrows(InvalidOrderStateException.class, () -> order.removeItem(item.getId()));
    }

    @Test
    void placeWhenNotPendingThrowsInvalidOrderStateException() {
        order.addItem(new OrderItem("prod-1", 2, Money.usd(new BigDecimal("5.00"))));
        order.pay();
        assertThrows(InvalidOrderStateException.class, () -> order.place());
    }
}
