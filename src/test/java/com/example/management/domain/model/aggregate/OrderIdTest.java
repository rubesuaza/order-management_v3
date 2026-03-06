package com.example.management.domain.model.aggregate;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OrderIdTest {

    @Test
    void generate_createsValidOrderId() {
        OrderId id = OrderId.generate();
        assertNotNull(id.getValue());
    }

    @Test
    void of_createsOrderIdFromUuid() {
        UUID uuid = UUID.randomUUID();
        OrderId id = OrderId.of(uuid);
        assertEquals(uuid, id.getValue());
    }

    @Test
    void constructor_nullValue_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new OrderId(null));
    }

    @Test
    void equals_sameUuid_returnsTrue() {
        UUID uuid = UUID.randomUUID();
        OrderId a = OrderId.of(uuid);
        OrderId b = OrderId.of(uuid);
        assertEquals(a, b);
    }
}
