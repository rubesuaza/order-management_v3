package com.example.management.domain.model.valueobject;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AddressTest {

    @Test
    void equals_sameValues_returnsTrue() {
        Address a = new Address("Main St", "NYC", "10001", "US");
        Address b = new Address("Main St", "NYC", "10001", "US");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void equals_differentValues_returnsFalse() {
        Address a = new Address("Main St", "NYC", "10001", "US");
        Address b = new Address("Other St", "LA", "90001", "US");
        assertNotEquals(a, b);
    }
}
