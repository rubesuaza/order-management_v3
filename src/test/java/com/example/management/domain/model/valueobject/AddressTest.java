package com.example.management.domain.model.valueobject;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AddressTest {

    @Test
    void equalsSameValuesReturnsTrue() {
        Address a = new Address("Main St", "NYC", "10001", "US");
        Address b = new Address("Main St", "NYC", "10001", "US");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void equalsDifferentValuesReturnsFalse() {
        Address a = new Address("Main St", "NYC", "10001", "US");
        Address b = new Address("Other St", "LA", "90001", "US");
        assertNotEquals(a, b);
    }

    @Test
    void equalsNullReturnsFalse() {
        Address a = new Address("Main St", "NYC", "10001", "US");
        assertNotEquals(a, null);
        assertFalse(a.equals(null));
    }

    @Test
    void equalsDifferentTypeReturnsFalse() {
        Address a = new Address("Main St", "NYC", "10001", "US");
        assertFalse(a.equals("Main St"));
    }
}
