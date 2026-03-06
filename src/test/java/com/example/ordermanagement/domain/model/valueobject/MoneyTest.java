package com.example.ordermanagement.domain.model.valueobject;

import com.example.ordermanagement.domain.exception.CurrencyMismatchException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class MoneyTest {

    @Test
    void shouldCreateMoneyWithValidAmountAndCurrency() {
        Money money = new Money(new BigDecimal("10.50"), "USD");
        assertEquals(new BigDecimal("10.50"), money.getAmount());
        assertEquals("USD", money.getCurrency());
    }

    @Test
    void shouldAddMoneyWithSameCurrency() {
        Money a = new Money(new BigDecimal("10.00"), "USD");
        Money b = new Money(new BigDecimal("5.50"), "USD");
        Money result = a.add(b);
        assertEquals(new BigDecimal("15.50"), result.getAmount());
        assertEquals("USD", result.getCurrency());
    }

    @Test
    void shouldThrowCurrencyMismatchExceptionWhenAddingDifferentCurrencies() {
        Money usd = new Money(new BigDecimal("10.00"), "USD");
        Money eur = new Money(new BigDecimal("5.00"), "EUR");
        assertThrows(CurrencyMismatchException.class, () -> usd.add(eur));
    }

    @Test
    void shouldMultiplyAndReturnNewInstance() {
        Money money = new Money(new BigDecimal("10.00"), "USD");
        Money result = money.multiply(2);
        assertEquals(new BigDecimal("20.00"), result.getAmount());
        assertEquals("USD", result.getCurrency());
        assertNotSame(money, result);
    }

    @Test
    void shouldBeImmutable() {
        Money money = new Money(new BigDecimal("10.00"), "USD");
        Money added = money.add(new Money(new BigDecimal("5.00"), "USD"));
        assertEquals(new BigDecimal("10.00"), money.getAmount());
    }

    @Test
    void shouldSupportEqualsAndHashCode() {
        Money a = new Money(new BigDecimal("10.00"), "USD");
        Money b = new Money(new BigDecimal("10.00"), "USD");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void shouldThrowWhenAmountIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new Money(null, "USD"));
    }

    @Test
    void shouldThrowWhenCurrencyIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new Money(new BigDecimal("10.00"), null));
    }

    @Test
    void shouldThrowWhenCurrencyIsBlank() {
        assertThrows(IllegalArgumentException.class, () -> new Money(new BigDecimal("10.00"), ""));
    }

    @Test
    void shouldMultiplyWithBigDecimal() {
        Money money = new Money(new BigDecimal("10.00"), "USD");
        Money result = money.multiply(new BigDecimal("2.5"));
        assertEquals(new BigDecimal("25.00"), result.getAmount());
        assertEquals("USD", result.getCurrency());
    }
}
