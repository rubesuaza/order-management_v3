package com.example.management.domain.valueobject;

import com.example.management.domain.exception.CurrencyMismatchException;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Immutable value object for monetary amounts.
 * Supports addition, subtraction, and multiplication with currency consistency.
 */
public final class Money {

    private static final String DEFAULT_CURRENCY = "USD";

    private final BigDecimal amount;
    private final String currency;

    public Money(BigDecimal amount) {
        this(amount, DEFAULT_CURRENCY);
    }

    public Money(BigDecimal amount, String currency) {
        this.amount = amount == null ? BigDecimal.ZERO : amount;
        this.currency = currency == null || currency.isBlank() ? DEFAULT_CURRENCY : currency;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public Money add(Money other) {
        ensureSameCurrency(other);
        return new Money(this.amount.add(other.amount), this.currency);
    }

    public Money subtract(Money other) {
        ensureSameCurrency(other);
        return new Money(this.amount.subtract(other.amount), this.currency);
    }

    public Money multiply(int factor) {
        return new Money(this.amount.multiply(BigDecimal.valueOf(factor)), this.currency);
    }

    private void ensureSameCurrency(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new CurrencyMismatchException(
                    "currency mismatch: cannot operate on " + this.currency + " and " + other.currency);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Money money = (Money) o;
        return amount.compareTo(money.amount) == 0 && Objects.equals(currency, money.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, currency);
    }
}
