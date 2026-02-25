package com.example.management.domain.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Address Value Object")
class AddressTest {

    @Nested
    @DisplayName("creation")
    class Creation {
        @Test
        void createsWithAllFields() {
            Address address = new Address("Main St 1", "New York", "10001", "USA");
            assertThat(address.getStreet()).isEqualTo("Main St 1");
            assertThat(address.getCity()).isEqualTo("New York");
            assertThat(address.getZipCode()).isEqualTo("10001");
            assertThat(address.getCountry()).isEqualTo("USA");
        }
    }

    @Nested
    @DisplayName("equality")
    class Equality {
        @Test
        void equalsWhenAllFieldsMatch() {
            Address a = new Address("St 1", "City", "ZIP", "Country");
            Address b = new Address("St 1", "City", "ZIP", "Country");
            assertThat(a).isEqualTo(b);
            assertThat(a.hashCode()).isEqualTo(b.hashCode());
        }

        @Test
        void notEqualsWhenAnyFieldDiffers() {
            Address a = new Address("St 1", "City", "ZIP", "Country");
            Address b = new Address("St 2", "City", "ZIP", "Country");
            assertThat(a).isNotEqualTo(b);
        }
    }
}
