package com.example.management.domain.valueobject;

import java.util.Objects;

/**
 * Immutable value object for address.
 */
public final class Address {

    private final String street;
    private final String city;
    private final String zipCode;
    private final String country;

    public Address(String street, String city, String zipCode, String country) {
        this.street = requireNonBlank(street, "Street cannot be null or empty");
        this.city = requireNonBlank(city, "City cannot be null or empty");
        this.zipCode = requireNonBlank(zipCode, "Zip code cannot be null or empty");
        this.country = requireNonBlank(country, "Country cannot be null or empty");
    }

    private static String requireNonBlank(String value, String message) {
        if (value == null) {
            throw new IllegalArgumentException(message);
        }
        if (value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }

    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }

    public String getZipCode() {
        return zipCode;
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
                && Objects.equals(zipCode, address.zipCode)
                && Objects.equals(country, address.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(street, city, zipCode, country);
    }
}
