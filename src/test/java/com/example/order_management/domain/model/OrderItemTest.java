package com.example.order_management.domain.model;

import com.example.order_management.domain.exception.InvalidItemException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderItemTest {

    @Test
    void shouldCreateValidOrderItem() {
        UUID productId = UUID.randomUUID();
        Money unitPrice = new Money(new BigDecimal("25.50"));
        
        OrderItem item = new OrderItem(productId, 3, unitPrice);
        
        assertThat(item.getProductId()).isEqualTo(productId);
        assertThat(item.getQuantity()).isEqualTo(3);
        assertThat(item.getUnitPrice()).isEqualTo(unitPrice);
    }

    @Test
    void shouldCalculateSubtotalCorrectly() {
        UUID productId = UUID.randomUUID();
        Money unitPrice = new Money(new BigDecimal("10.00"));
        OrderItem item = new OrderItem(productId, 5, unitPrice);
        
        Money subtotal = item.calculateSubtotal();
        
        assertThat(subtotal.getAmount()).isEqualByComparingTo(new BigDecimal("50.00"));
        assertThat(subtotal.getCurrency()).isEqualTo("USD");
    }

    @Test
    void shouldThrowExceptionWhenQuantityIsZero() {
        UUID productId = UUID.randomUUID();
        Money unitPrice = new Money(new BigDecimal("10.00"));
        
        assertThatThrownBy(() -> new OrderItem(productId, 0, unitPrice))
                .isInstanceOf(InvalidItemException.class)
                .hasMessageContaining("Quantity must be greater than zero");
    }

    @Test
    void shouldThrowExceptionWhenQuantityIsNegative() {
        UUID productId = UUID.randomUUID();
        Money unitPrice = new Money(new BigDecimal("10.00"));
        
        assertThatThrownBy(() -> new OrderItem(productId, -1, unitPrice))
                .isInstanceOf(InvalidItemException.class)
                .hasMessageContaining("Quantity must be greater than zero");
    }

    @Test
    void shouldThrowExceptionWhenUnitPriceIsNull() {
        UUID productId = UUID.randomUUID();
        
        assertThatThrownBy(() -> new OrderItem(productId, 1, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unit price cannot be null");
    }

    @Test
    void shouldThrowExceptionWhenProductIdIsNull() {
        Money unitPrice = new Money(new BigDecimal("10.00"));
        
        assertThatThrownBy(() -> new OrderItem(null, 1, unitPrice))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Product ID cannot be null");
    }
}
