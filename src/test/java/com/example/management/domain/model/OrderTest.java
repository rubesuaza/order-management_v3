package com.example.management.domain.model;

import com.example.management.domain.OrderStatus;
import com.example.management.domain.exception.InvalidOrderStateException;
import com.example.management.domain.valueobject.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Order Aggregate")
class OrderTest {

    private static final UUID CUSTOMER_ID = UUID.randomUUID();
    private static final UUID PRODUCT_ID = UUID.randomUUID();

    private static OrderItem item(BigDecimal unitPrice, int qty) {
        return new OrderItem(PRODUCT_ID, qty, new Money(unitPrice));
    }

    @Nested
    @DisplayName("creation")
    class Creation {
        @Test
        void createsWithAtLeastOneItemAndComputesTotal() {
            Order order = new Order(
                    UUID.randomUUID(),
                    CUSTOMER_ID,
                    List.of(item(new BigDecimal("5.00"), 2))
            );
            assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);
            assertThat(order.getTotalAmount().getAmount()).isEqualByComparingTo("10.00");
            assertThat(order.getItems()).hasSize(1);
        }

        @Test
        void rejectsEmptyItems() {
            assertThatThrownBy(() -> new Order(UUID.randomUUID(), CUSTOMER_ID, List.of()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("at least one");
        }

        @Test
        void totalIsSumOfLineTotals() {
            Order order = new Order(
                    UUID.randomUUID(),
                    CUSTOMER_ID,
                    List.of(
                            item(new BigDecimal("3.00"), 2),
                            item(new BigDecimal("4.00"), 1)
                    )
            );
            assertThat(order.getTotalAmount().getAmount()).isEqualByComparingTo("10.00");
        }
    }

    @Nested
    @DisplayName("mark as paid")
    class MarkAsPaid {
        @Test
        void allowsPaidWhenTotalAtLeast10Usd() {
            Order order = new Order(
                    UUID.randomUUID(),
                    CUSTOMER_ID,
                    List.of(item(new BigDecimal("10.00"), 1))
            );
            order.markAsPaid();
            assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);
        }

        @Test
        void rejectsPaidWhenTotalBelow10Usd() {
            Order order = new Order(
                    UUID.randomUUID(),
                    CUSTOMER_ID,
                    List.of(item(new BigDecimal("5.00"), 1))
            );
            assertThatThrownBy(order::markAsPaid)
                    .isInstanceOf(InvalidOrderStateException.class)
                    .hasMessageContaining("10");
        }

        @Test
        void rejectsPaidWhenNotPending() {
            Order order = new Order(
                    UUID.randomUUID(),
                    CUSTOMER_ID,
                    List.of(item(new BigDecimal("15.00"), 1))
            );
            order.markAsPaid();
            assertThatThrownBy(order::markAsPaid)
                    .isInstanceOf(InvalidOrderStateException.class);
        }
    }

    @Nested
    @DisplayName("ship")
    class Ship {
        @Test
        void allowsShipWhenPaid() {
            Order order = new Order(
                    UUID.randomUUID(),
                    CUSTOMER_ID,
                    List.of(item(new BigDecimal("20.00"), 1))
            );
            order.markAsPaid();
            order.ship();
            assertThat(order.getStatus()).isEqualTo(OrderStatus.SHIPPED);
        }

        @Test
        void rejectsShipWhenNotPaid() {
            Order order = new Order(
                    UUID.randomUUID(),
                    CUSTOMER_ID,
                    List.of(item(new BigDecimal("20.00"), 1))
            );
            assertThatThrownBy(order::ship)
                    .isInstanceOf(InvalidOrderStateException.class)
                    .hasMessageContaining("PAID");
        }
    }

    @Nested
    @DisplayName("cancel")
    class Cancel {
        @Test
        void allowsCancelWhenPending() {
            Order order = new Order(
                    UUID.randomUUID(),
                    CUSTOMER_ID,
                    List.of(item(new BigDecimal("20.00"), 1))
            );
            order.cancel();
            assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        }

        @Test
        void allowsCancelWhenPaid() {
            Order order = new Order(
                    UUID.randomUUID(),
                    CUSTOMER_ID,
                    List.of(item(new BigDecimal("20.00"), 1))
            );
            order.markAsPaid();
            order.cancel();
            assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        }

        @Test
        void rejectsCancelWhenShipped() {
            Order order = new Order(
                    UUID.randomUUID(),
                    CUSTOMER_ID,
                    List.of(item(new BigDecimal("20.00"), 1))
            );
            order.markAsPaid();
            order.ship();
            assertThatThrownBy(order::cancel)
                    .isInstanceOf(InvalidOrderStateException.class)
                    .hasMessageContaining("SHIPPED");
        }
    }
}
