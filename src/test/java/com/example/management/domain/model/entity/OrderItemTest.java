package com.example.management.domain.model.entity;

import com.example.management.domain.exception.InvalidItemException;
import com.example.management.domain.model.valueobject.Money;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class OrderItemTest {

    @Test
    void constructor_validInput_createsOrderItem() {
        OrderItem item = new OrderItem("prod-1", 2, Money.usd(new BigDecimal("5.00")));

        assertEquals("prod-1", item.getProductId());
        assertEquals(2, item.getQuantity());
        assertEquals(new BigDecimal("5.00"), item.getUnitPrice().getAmount());
        assertEquals(new BigDecimal("10.00"), item.getLineTotal().getAmount());
    }

    @Test
    void constructor_quantityZero_throwsInvalidItemException() {
        assertThrows(InvalidItemException.class,
                () -> new OrderItem("prod-1", 0, Money.usd(BigDecimal.ONE)));
    }

    @Test
    void constructor_quantityNegative_throwsInvalidItemException() {
        assertThrows(InvalidItemException.class,
                () -> new OrderItem("prod-1", -1, Money.usd(BigDecimal.ONE)));
    }

    @Test
    void constructor_negativeUnitPrice_throwsInvalidItemException() {
        assertThrows(InvalidItemException.class,
                () -> new OrderItem("prod-1", 1, Money.usd(new BigDecimal("-5.00"))));
    }

    @Test
    void constructor_blankProductId_throwsInvalidItemException() {
        assertThrows(InvalidItemException.class,
                () -> new OrderItem("", 1, Money.usd(BigDecimal.ONE)));
    }

    @Test
    void getLineTotal_multipliesQuantityByUnitPrice() {
        OrderItem item = new OrderItem("prod-1", 3, Money.usd(new BigDecimal("4.99")));
        assertEquals(new BigDecimal("14.97"), item.getLineTotal().getAmount());
    }
}
