package com.example.management.domain.model.entity;

import com.example.management.domain.exception.InvalidItemException;
import com.example.management.domain.model.valueobject.Money;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class OrderItemTest {

    @Test
    void constructor_positiveQuantity_createsItem() {
        OrderItem item = new OrderItem(1, "PROD-001", 2, Money.usd(BigDecimal.valueOf(5.00)));
        assertEquals(1, item.getItemId());
        assertEquals("PROD-001", item.getProductId());
        assertEquals(2, item.getQuantity());
        assertEquals(0, new BigDecimal("5.00").compareTo(item.getUnitPrice().getAmount()));
        assertEquals(0, new BigDecimal("10.00").compareTo(item.getLineTotal().getAmount()));
    }

    @Test
    void constructor_zeroQuantity_throwsInvalidItemException() {
        assertThrows(InvalidItemException.class,
                () -> new OrderItem(1, "PROD-001", 0, Money.usd(BigDecimal.valueOf(5.00))));
    }

    @Test
    void constructor_negativeQuantity_throwsInvalidItemException() {
        assertThrows(InvalidItemException.class,
                () -> new OrderItem(1, "PROD-001", -1, Money.usd(BigDecimal.valueOf(5.00))));
    }

    @Test
    void constructor_negativeUnitPrice_throwsInvalidItemException() {
        assertThrows(InvalidItemException.class,
                () -> new OrderItem(1, "PROD-001", 1, Money.usd(BigDecimal.valueOf(-5.00))));
    }

    @Test
    void constructor_blankProductId_throwsInvalidItemException() {
        assertThrows(InvalidItemException.class,
                () -> new OrderItem(1, "  ", 1, Money.usd(BigDecimal.ONE)));
    }

    @Test
    void constructor_nullProductId_throwsInvalidItemException() {
        assertThrows(InvalidItemException.class,
                () -> new OrderItem(1, null, 1, Money.usd(BigDecimal.ONE)));
    }

    @Test
    void constructor_nullUnitPrice_throwsInvalidItemException() {
        assertThrows(InvalidItemException.class,
                () -> new OrderItem(1, "PROD-001", 1, null));
    }

    @Test
    void getLineTotal_calculatesCorrectly() {
        OrderItem item = new OrderItem(1, "PROD-001", 3, Money.usd(BigDecimal.valueOf(7.50)));
        assertEquals(0, new BigDecimal("22.50").compareTo(item.getLineTotal().getAmount()));
    }

    @Test
    void equals_sameItemId_returnsTrue() {
        OrderItem a = new OrderItem(1, "PROD-001", 1, Money.usd(BigDecimal.ONE));
        OrderItem b = new OrderItem(1, "PROD-002", 2, Money.usd(BigDecimal.TEN));
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }
}
