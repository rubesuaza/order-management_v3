package com.example.management.domain.model.valueobject;

import com.example.management.domain.exception.CurrencyMismatchException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class MoneyTest {

    @Test
    void add_sameCurrency_returnsNewInstanceWithSum() {
        Money a = Money.usd(new BigDecimal("10.00"));
        Money b = Money.usd(new BigDecimal("5.50"));
        Money result = a.add(b);

        assertNotSame(a, result);
        assertEquals(new BigDecimal("15.50"), result.getAmount());
        assertEquals("USD", result.getCurrency());
    }

    @Test
    void add_differentCurrencies_throwsCurrencyMismatchException() {
        Money usd = Money.usd(new BigDecimal("10.00"));
        Money eur = Money.of(new BigDecimal("5.00"), "EUR");

        assertThrows(CurrencyMismatchException.class, () -> usd.add(eur));
    }

    @Test
    void multiply_returnsNewInstance() {
        Money m = Money.usd(new BigDecimal("10.00"));
        Money result = m.multiply(3);

        assertNotSame(m, result);
        assertEquals(new BigDecimal("30.00"), result.getAmount());
    }

    @Test
    void isGreaterThanOrEqual_sameCurrency_comparesCorrectly() {
        Money ten = Money.usd(new BigDecimal("10.00"));
        Money five = Money.usd(new BigDecimal("5.00"));

        assertTrue(ten.isGreaterThanOrEqual(five));
        assertTrue(ten.isGreaterThanOrEqual(ten));
        assertFalse(five.isGreaterThanOrEqual(ten));
    }

    @Test
    void isGreaterThanOrEqual_differentCurrencies_throwsCurrencyMismatchException() {
        Money usd = Money.usd(new BigDecimal("10.00"));
        Money eur = Money.of(new BigDecimal("5.00"), "EUR");

        assertThrows(CurrencyMismatchException.class, () -> usd.isGreaterThanOrEqual(eur));
    }

    @Test
    void constructor_nullAmount_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new Money(null, "USD"));
    }

    @Test
    void constructor_nullCurrency_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new Money(BigDecimal.ONE, null));
    }

    @Test
    void equals_sameAmountAndCurrency_returnsTrue() {
        Money a = Money.usd(new BigDecimal("10.00"));
        Money b = Money.usd(new BigDecimal("10.00"));
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void constructor_blankCurrency_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new Money(BigDecimal.ONE, ""));
        assertThrows(IllegalArgumentException.class, () -> new Money(BigDecimal.ONE, "   "));
    }

    @Test
    void multiply_withBigDecimal_returnsNewInstance() {
        Money m = Money.usd(new BigDecimal("10.00"));
        Money result = m.multiply(new BigDecimal("2.5"));
        assertNotSame(m, result);
        assertEquals(new BigDecimal("25.00"), result.getAmount());
        assertEquals("USD", result.getCurrency());
    }
}
