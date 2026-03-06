package com.example.management.domain.model.valueobject;

import com.example.management.domain.exception.CurrencyMismatchException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class MoneyTest {

    @Test
    void add_sameCurrency_returnsNewInstance() {
        Money a = Money.usd(BigDecimal.valueOf(10.00));
        Money b = Money.usd(BigDecimal.valueOf(5.00));
        Money result = a.add(b);
        assertEquals(0, new BigDecimal("15.00").compareTo(result.getAmount()));
        assertEquals("USD", result.getCurrency());
        assertNotSame(a, result);
    }

    @Test
    void add_differentCurrencies_throwsCurrencyMismatchException() {
        Money usd = Money.usd(BigDecimal.ONE);
        Money eur = new Money(BigDecimal.ONE, "EUR");
        assertThrows(CurrencyMismatchException.class, () -> usd.add(eur));
    }

    @Test
    void multiply_returnsNewInstance() {
        Money m = Money.usd(BigDecimal.valueOf(10.00));
        Money result = m.multiply(BigDecimal.valueOf(3));
        assertEquals(0, new BigDecimal("30.00").compareTo(result.getAmount()));
        assertNotSame(m, result);
    }

    @Test
    void isGreaterThanOrEqual_sameCurrency_comparesCorrectly() {
        Money ten = Money.usd(BigDecimal.valueOf(10.00));
        Money five = Money.usd(BigDecimal.valueOf(5.00));
        assertTrue(ten.isGreaterThanOrEqual(five));
        assertTrue(ten.isGreaterThanOrEqual(ten));
        assertFalse(five.isGreaterThanOrEqual(ten));
    }

    @Test
    void isGreaterThanOrEqual_differentCurrencies_throwsCurrencyMismatchException() {
        Money usd = Money.usd(BigDecimal.ONE);
        Money eur = new Money(BigDecimal.ONE, "EUR");
        assertThrows(CurrencyMismatchException.class, () -> usd.isGreaterThanOrEqual(eur));
    }

    @Test
    void constructor_nullAmount_throws() {
        assertThrows(IllegalArgumentException.class, () -> new Money(null, "USD"));
    }

    @Test
    void constructor_blankCurrency_throws() {
        assertThrows(IllegalArgumentException.class, () -> new Money(BigDecimal.ONE, ""));
    }
}
