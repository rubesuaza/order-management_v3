package com.example.ordermanagement.domain.model.valueobject;

import java.util.Objects;

/**
 * Immutable value object representing a delivery/shipping address.
 */
public final class Address {

    private final String street;
    private final String city;
    private final String postalCode;
    private final String country;

    public Address(String street, String city, String postalCode, String country) {
        this.street = street != null ? street : "";
        this.city = city != null ? city : "";
        this.postalCode = postalCode != null ? postalCode : "";
        this.country = country != null ? country : "";
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

    /**
     * Fluent Builder for Address (Design Pattern: Builder).
     */
    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String street = "";
        private String city = "";
        private String postalCode = "";
        private String country = "";

        private Builder() {
        }

        public Builder street(String street) {
            this.street = street != null ? street : "";
            return this;
        }

        public Builder city(String city) {
            this.city = city != null ? city : "";
            return this;
        }

        public Builder postalCode(String postalCode) {
            this.postalCode = postalCode != null ? postalCode : "";
            return this;
        }

        public Builder country(String country) {
            this.country = country != null ? country : "";
            return this;
        }

        public Address build() {
            return new Address(street, city, postalCode, country);
        }
    }
}
