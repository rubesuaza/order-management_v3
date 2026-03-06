package com.example.management.domain.model.valueobject;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AddressTest {

    @Test
    void equals_sameValues_returnsTrue() {
        Address a = new Address("Street 1", "City", "12345", "US");
        Address b = new Address("Street 1", "City", "12345", "US");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void equals_differentValues_returnsFalse() {
        Address a = new Address("Street 1", "City", "12345", "US");
        Address b = new Address("Street 2", "City", "12345", "US");
        assertNotEquals(a, b);
    }
}
