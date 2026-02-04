package com.example.management.domain.model;

import com.example.management.domain.exception.CurrencyMismatchException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MoneyTest {

    private static final String USD = "USD";
    private static final String EUR = "EUR";

    @Test
    void shouldCreateMoneyWithAmountAndDefaultCurrency() {
        Money money = new Money(new BigDecimal("99.99"));
        assertThat(money.getAmount()).isEqualByComparingTo(new BigDecimal("99.99"));
        assertThat(money.getCurrency()).isEqualTo(USD);
    }

    @Test
    void shouldCreateMoneyWithAmountAndCurrency() {
        Money money = new Money(new BigDecimal("50.00"), EUR);
        assertThat(money.getAmount()).isEqualByComparingTo(new BigDecimal("50.00"));
        assertThat(money.getCurrency()).isEqualTo(EUR);
    }

    @Test
    void add_shouldSumAmountsWhenSameCurrency() {
        Money a = new Money(new BigDecimal("10.00"), USD);
        Money b = new Money(new BigDecimal("5.50"), USD);
        Money result = a.add(b);
        assertThat(result.getAmount()).isEqualByComparingTo(new BigDecimal("15.50"));
        assertThat(result.getCurrency()).isEqualTo(USD);
    }

    @Test
    void add_shouldThrowWhenCurrenciesDiffer() {
        Money usd = new Money(new BigDecimal("10.00"), USD);
        Money eur = new Money(new BigDecimal("5.00"), EUR);
        assertThatThrownBy(() -> usd.add(eur))
                .isInstanceOf(CurrencyMismatchException.class)
                .hasMessageContaining("Currency");
    }

    @Test
    void subtract_shouldSubtractAmountsWhenSameCurrency() {
        Money a = new Money(new BigDecimal("20.00"), USD);
        Money b = new Money(new BigDecimal("7.25"), USD);
        Money result = a.subtract(b);
        assertThat(result.getAmount()).isEqualByComparingTo(new BigDecimal("12.75"));
        assertThat(result.getCurrency()).isEqualTo(USD);
    }

    @Test
    void subtract_shouldThrowWhenCurrenciesDiffer() {
        Money usd = new Money(new BigDecimal("20.00"), USD);
        Money eur = new Money(new BigDecimal("5.00"), EUR);
        assertThatThrownBy(() -> usd.subtract(eur))
                .isInstanceOf(CurrencyMismatchException.class);
    }

    @Test
    void multiply_shouldMultiplyAmountByScalar() {
        Money money = new Money(new BigDecimal("10.50"), USD);
        Money result = money.multiply(3);
        assertThat(result.getAmount()).isEqualByComparingTo(new BigDecimal("31.50"));
        assertThat(result.getCurrency()).isEqualTo(USD);
    }

    @Test
    void equals_and_hashCode_shouldBeBasedOnAmountAndCurrency() {
        Money a = new Money(new BigDecimal("10.00"), USD);
        Money b = new Money(new BigDecimal("10.00"), USD);
        Money c = new Money(new BigDecimal("10.00"), EUR);
        assertThat(a).isEqualTo(b);
        assertThat(a).hasSameHashCodeAs(b);
        assertThat(a).isNotEqualTo(c);
    }
}
