package com.example.management.domain.model.aggregate;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OrderIdTest {

    @Test
    void generateCreatesValidOrderId() {
        OrderId id = OrderId.generate();
        assertNotNull(id.getValue());
    }

    @Test
    void ofCreatesOrderIdFromUuid() {
        UUID uuid = UUID.randomUUID();
        OrderId id = OrderId.of(uuid);
        assertEquals(uuid, id.getValue());
    }

    @Test
    void constructorNullValueThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new OrderId(null));
    }

    @Test
    void equalsSameUuidReturnsTrue() {
        UUID uuid = UUID.randomUUID();
        OrderId a = OrderId.of(uuid);
        OrderId b = OrderId.of(uuid);
        assertEquals(a, b);
    }

    @Test
    void equalsNullReturnsFalse() {
        OrderId id = OrderId.generate();
        assertNotEquals(id, null);
        assertFalse(id.equals(null));
    }

    @Test
    void equalsDifferentTypeReturnsFalse() {
        OrderId id = OrderId.generate();
        assertNotEquals(id, id.getValue());
        assertFalse(id.equals("string"));
    }

    @Test
    void equalsDifferentValuesReturnsFalse() {
        OrderId a = OrderId.generate();
        OrderId b = OrderId.generate();
        assertNotEquals(a, b);
    }
}
