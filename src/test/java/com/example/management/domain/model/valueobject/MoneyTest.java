package com.example.management.domain.model.valueobject;

import com.example.management.domain.exception.CurrencyMismatchException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class MoneyTest {

    @Test
    void addSameCurrencyReturnsNewInstanceWithSum() {
        Money a = Money.usd(new BigDecimal("10.00"));
        Money b = Money.usd(new BigDecimal("5.50"));
        Money result = a.add(b);

        assertNotSame(a, result);
        assertEquals(new BigDecimal("15.50"), result.getAmount());
        assertEquals("USD", result.getCurrency());
    }

    @Test
    void addDifferentCurrenciesThrowsCurrencyMismatchException() {
        Money usd = Money.usd(new BigDecimal("10.00"));
        Money eur = Money.of(new BigDecimal("5.00"), "EUR");

        assertThrows(CurrencyMismatchException.class, () -> usd.add(eur));
    }

    @Test
    void multiplyByIntegerReturnsNewInstanceWithCorrectAmount() {
        Money m = Money.usd(new BigDecimal("10.00"));
        Money result = m.multiply(3);

        assertNotSame(m, result);
        assertEquals(new BigDecimal("30.00"), result.getAmount());
    }

    @Test
    void multiplyByZeroReturnsZeroAmount() {
        Money m = Money.usd(new BigDecimal("10.00"));
        Money result = m.multiply(0);
        assertEquals(BigDecimal.ZERO.setScale(2), result.getAmount());
        assertEquals("USD", result.getCurrency());
    }

    @Test
    void multiplyByOneReturnsSameAmount() {
        Money m = Money.usd(new BigDecimal("10.00"));
        Money result = m.multiply(1);
        assertEquals(new BigDecimal("10.00"), result.getAmount());
        assertEquals("USD", result.getCurrency());
    }

    @Test
    void multiplyByNegativeReturnsNegativeAmount() {
        Money m = Money.usd(new BigDecimal("10.00"));
        Money result = m.multiply(-2);
        assertEquals(new BigDecimal("-20.00"), result.getAmount());
        assertEquals("USD", result.getCurrency());
    }

    @Test
    void multiplyByBigDecimalWithFractionAltersScale() {
        Money m = Money.usd(new BigDecimal("10.00"));
        Money result = m.multiply(new BigDecimal("0.333"));
        assertNotNull(result.getAmount());
        assertEquals("USD", result.getCurrency());
    }

    @Test
    void isGreaterThanOrEqualSameCurrencyComparesCorrectly() {
        Money ten = Money.usd(new BigDecimal("10.00"));
        Money five = Money.usd(new BigDecimal("5.00"));

        assertTrue(ten.isGreaterThanOrEqual(five));
        assertTrue(ten.isGreaterThanOrEqual(ten));
        assertFalse(five.isGreaterThanOrEqual(ten));
    }

    @Test
    void isGreaterThanOrEqualDifferentCurrenciesThrowsCurrencyMismatchException() {
        Money usd = Money.usd(new BigDecimal("10.00"));
        Money eur = Money.of(new BigDecimal("5.00"), "EUR");

        assertThrows(CurrencyMismatchException.class, () -> usd.isGreaterThanOrEqual(eur));
    }

    @Test
    void constructorNullAmountThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new Money(null, "USD"));
    }

    @Test
    void constructorNullCurrencyThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new Money(BigDecimal.ONE, null));
    }

    @Test
    void equalsSameAmountAndCurrencyReturnsTrue() {
        Money a = Money.usd(new BigDecimal("10.00"));
        Money b = Money.usd(new BigDecimal("10.00"));
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void constructorBlankCurrencyThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new Money(BigDecimal.ONE, ""));
        assertThrows(IllegalArgumentException.class, () -> new Money(BigDecimal.ONE, "   "));
    }

    @Test
    void multiplyWithBigDecimalReturnsNewInstance() {
        Money m = Money.usd(new BigDecimal("10.00"));
        Money result = m.multiply(new BigDecimal("2.5"));
        assertNotSame(m, result);
        assertEquals(new BigDecimal("25.00"), result.getAmount());
        assertEquals("USD", result.getCurrency());
    }

    @Test
    void equalsNullReturnsFalse() {
        Money m = Money.usd(BigDecimal.TEN);
        assertNotEquals(m, null);
        assertFalse(m.equals(null));
    }

    @Test
    void equalsDifferentTypeReturnsFalse() {
        Money m = Money.usd(BigDecimal.TEN);
        assertFalse(m.equals("10.00"));
    }

    @Test
    void equalsDifferentAmountReturnsFalse() {
        Money a = Money.usd(new BigDecimal("10.00"));
        Money b = Money.usd(new BigDecimal("20.00"));
        assertNotEquals(a, b);
    }

    @Test
    void equalsDifferentCurrencyReturnsFalse() {
        Money a = Money.usd(BigDecimal.TEN);
        Money b = Money.of(BigDecimal.TEN, "EUR");
        assertNotEquals(a, b);
    }
}
