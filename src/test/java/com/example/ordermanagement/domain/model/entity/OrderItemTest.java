package com.example.ordermanagement.domain.model.entity;

import com.example.ordermanagement.domain.exception.InvalidItemException;
import com.example.ordermanagement.domain.model.valueobject.Money;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OrderItemTest {

    @Test
    void shouldCreateOrderItemWithValidQuantityAndPrice() {
        Money unitPrice = new Money(new BigDecimal("5.00"), "USD");
        OrderItem item = new OrderItem(UUID.randomUUID(), "prod-1", 2, unitPrice);
        assertEquals("prod-1", item.getProductId());
        assertEquals(2, item.getQuantity());
        assertEquals(unitPrice, item.getUnitPrice());
    }

    @Test
    void shouldThrowInvalidItemExceptionWhenQuantityIsZero() {
        Money unitPrice = new Money(new BigDecimal("5.00"), "USD");
        assertThrows(InvalidItemException.class, () ->
                new OrderItem(UUID.randomUUID(), "prod-1", 0, unitPrice));
    }

    @Test
    void shouldThrowInvalidItemExceptionWhenQuantityIsNegative() {
        Money unitPrice = new Money(new BigDecimal("5.00"), "USD");
        assertThrows(InvalidItemException.class, () ->
                new OrderItem(UUID.randomUUID(), "prod-1", -1, unitPrice));
    }

    @Test
    void shouldThrowInvalidItemExceptionWhenUnitPriceIsNegative() {
        Money negativePrice = new Money(new BigDecimal("-5.00"), "USD");
        assertThrows(InvalidItemException.class, () ->
                new OrderItem(UUID.randomUUID(), "prod-1", 1, negativePrice));
    }

    @Test
    void shouldCalculateLineTotal() {
        Money unitPrice = new Money(new BigDecimal("5.00"), "USD");
        OrderItem item = new OrderItem(UUID.randomUUID(), "prod-1", 3, unitPrice);
        Money lineTotal = item.getLineTotal();
        assertEquals(new BigDecimal("15.00"), lineTotal.getAmount());
        assertEquals("USD", lineTotal.getCurrency());
    }

    @Test
    void shouldThrowWhenIdIsNull() {
        Money unitPrice = new Money(new BigDecimal("5.00"), "USD");
        assertThrows(IllegalArgumentException.class, () ->
                new OrderItem(null, "prod-1", 1, unitPrice));
    }

    @Test
    void shouldThrowWhenProductIdIsNull() {
        Money unitPrice = new Money(new BigDecimal("5.00"), "USD");
        assertThrows(IllegalArgumentException.class, () ->
                new OrderItem(UUID.randomUUID(), null, 1, unitPrice));
    }

    @Test
    void shouldThrowWhenProductIdIsBlank() {
        Money unitPrice = new Money(new BigDecimal("5.00"), "USD");
        assertThrows(IllegalArgumentException.class, () ->
                new OrderItem(UUID.randomUUID(), "   ", 1, unitPrice));
    }

    @Test
    void shouldThrowWhenUnitPriceIsNull() {
        assertThrows(IllegalArgumentException.class, () ->
                new OrderItem(UUID.randomUUID(), "prod-1", 1, null));
    }

    @Test
    void shouldSupportEqualsAndHashCode() {
        UUID id = UUID.randomUUID();
        Money unitPrice = new Money(new BigDecimal("5.00"), "USD");
        OrderItem a = new OrderItem(id, "prod-1", 1, unitPrice);
        OrderItem b = new OrderItem(id, "prod-2", 2, unitPrice);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }
}
