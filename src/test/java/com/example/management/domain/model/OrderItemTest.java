package com.example.management.domain.model;

import com.example.management.domain.exception.InvalidItemException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderItemTest {

    private static final UUID PRODUCT_ID = UUID.randomUUID();
    private static final Money TEN_USD = new Money(new BigDecimal("10.00"));

    @Test
    void shouldCreateOrderItemWithValidQuantityAndPrice() {
        OrderItem item = new OrderItem(PRODUCT_ID, 2, TEN_USD);
        assertThat(item.getProductId()).isEqualTo(PRODUCT_ID);
        assertThat(item.getQuantity()).isEqualTo(2);
        assertThat(item.getUnitPrice()).isEqualTo(TEN_USD);
    }

    @Test
    void getLineTotal_shouldReturnUnitPriceTimesQuantity() {
        OrderItem item = new OrderItem(PRODUCT_ID, 3, new Money(new BigDecimal("5.50")));
        Money lineTotal = item.getLineTotal();
        assertThat(lineTotal.getAmount()).isEqualByComparingTo(new BigDecimal("16.50"));
        assertThat(lineTotal.getCurrency()).isEqualTo("USD");
    }

    @Test
    void shouldRejectZeroQuantity() {
        assertThatThrownBy(() -> new OrderItem(PRODUCT_ID, 0, TEN_USD))
                .isInstanceOf(InvalidItemException.class)
                .hasMessageContaining("quantity");
    }

    @Test
    void shouldRejectNegativeQuantity() {
        assertThatThrownBy(() -> new OrderItem(PRODUCT_ID, -1, TEN_USD))
                .isInstanceOf(InvalidItemException.class)
                .hasMessageContaining("quantity");
    }

    @Test
    void shouldRejectNegativeUnitPrice() {
        Money negativePrice = new Money(new BigDecimal("-1.00"));
        assertThatThrownBy(() -> new OrderItem(PRODUCT_ID, 1, negativePrice))
                .isInstanceOf(InvalidItemException.class)
                .hasMessageContaining("price");
    }

    @Test
    void shouldAcceptZeroUnitPrice() {
        Money zeroPrice = new Money(BigDecimal.ZERO);
        OrderItem item = new OrderItem(PRODUCT_ID, 1, zeroPrice);
        assertThat(item.getUnitPrice().getAmount()).isEqualByComparingTo(BigDecimal.ZERO);
    }
}
