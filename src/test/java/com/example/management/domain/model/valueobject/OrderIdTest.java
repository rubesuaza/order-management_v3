package com.example.management.domain.model.valueobject;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OrderIdTest {

    @Test
    void generate_createsValidId() {
        OrderId id = OrderId.generate();
        assertNotNull(id.getValue());
    }

    @Test
    void of_uuid_createsId() {
        UUID uuid = UUID.randomUUID();
        OrderId id = OrderId.of(uuid);
        assertEquals(uuid, id.getValue());
    }

    @Test
    void of_null_throws() {
        assertThrows(IllegalArgumentException.class, () -> new OrderId(null));
    }

    @Test
    void of_string_createsId() {
        String uuidStr = "550e8400-e29b-41d4-a716-446655440000";
        OrderId id = OrderId.of(uuidStr);
        assertEquals(UUID.fromString(uuidStr), id.getValue());
    }

    @Test
    void toString_returnsUuidString() {
        UUID uuid = UUID.randomUUID();
        OrderId id = OrderId.of(uuid);
        assertEquals(uuid.toString(), id.toString());
    }
}
