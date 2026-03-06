package com.example.ordermanagement.domain.model.valueobject;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OrderIdTest {

    @Test
    void shouldCreateOrderIdWithValidUuid() {
        UUID uuid = UUID.randomUUID();
        OrderId orderId = new OrderId(uuid);
        assertEquals(uuid, orderId.getValue());
    }

    @Test
    void shouldGenerateOrderId() {
        OrderId orderId = OrderId.generate();
        assertNotNull(orderId.getValue());
        assertNotNull(orderId.getValue().toString());
    }

    @Test
    void shouldThrowWhenValueIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new OrderId(null));
    }

    @Test
    void shouldSupportEqualsAndHashCode() {
        UUID uuid = UUID.randomUUID();
        OrderId a = new OrderId(uuid);
        OrderId b = new OrderId(uuid);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void shouldReturnToString() {
        UUID uuid = UUID.randomUUID();
        OrderId orderId = new OrderId(uuid);
        assertEquals(uuid.toString(), orderId.toString());
    }
}
