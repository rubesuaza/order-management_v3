package com.example.management.domain.model.valueobject;

import java.util.Objects;

/**
 * Immutable value object representing a shipping or billing address.
 * Supports Builder pattern for fluent construction.
 */
public final class Address {

    private final String street;
    private final String city;
    private final String postalCode;
    private final String country;

    public Address(String street, String city, String postalCode, String country) {
        this.street = street;
        this.city = city;
        this.postalCode = postalCode;
        this.country = country;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String street;
        private String city;
        private String postalCode;
        private String country;

        public Builder street(String street) {
            this.street = street;
            return this;
        }

        public Builder city(String city) {
            this.city = city;
            return this;
        }

        public Builder postalCode(String postalCode) {
            this.postalCode = postalCode;
            return this;
        }

        public Builder country(String country) {
            this.country = country;
            return this;
        }

        public Address build() {
            return new Address(street, city, postalCode, country);
        }
    }

    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getCountry() {
        return country;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return Objects.equals(street, address.street)
                && Objects.equals(city, address.city)
                && Objects.equals(postalCode, address.postalCode)
                && Objects.equals(country, address.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(street, city, postalCode, country);
    }
}
