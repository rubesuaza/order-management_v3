package com.example.ordermanagement.domain.model.valueobject;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AddressTest {

    @Test
    void shouldCreateAddressWithValidFields() {
        Address address = new Address("123 Main St", "New York", "10001", "USA");
        assertEquals("123 Main St", address.getStreet());
        assertEquals("New York", address.getCity());
        assertEquals("10001", address.getPostalCode());
        assertEquals("USA", address.getCountry());
    }

    @Test
    void shouldHandleNullFieldsAsEmptyString() {
        Address address = new Address(null, null, null, null);
        assertEquals("", address.getStreet());
        assertEquals("", address.getCity());
        assertEquals("", address.getPostalCode());
        assertEquals("", address.getCountry());
    }

    @Test
    void shouldSupportEqualsAndHashCode() {
        Address a = new Address("123 Main St", "NYC", "10001", "USA");
        Address b = new Address("123 Main St", "NYC", "10001", "USA");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void shouldNotBeEqualWhenFieldsDiffer() {
        Address a = new Address("123 Main St", "NYC", "10001", "USA");
        Address b = new Address("456 Oak Ave", "LA", "90001", "USA");
        assertNotEquals(a, b);
    }
}
