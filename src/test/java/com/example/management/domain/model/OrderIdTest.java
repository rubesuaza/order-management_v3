package com.example.management.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("OrderId Value Object")
class OrderIdTest {

    @Nested
    @DisplayName("creation")
    class Creation {
        @Test
        void createsWithValidUuid() {
            UUID value = UUID.randomUUID();
            OrderId orderId = new OrderId(value);
            assertThat(orderId.getValue()).isEqualTo(value);
        }

        @Test
        void rejectsNullValue() {
            assertThatThrownBy(() -> new OrderId(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("OrderId value");
        }
    }

    @Nested
    @DisplayName("equality")
    class Equality {
        @Test
        void equalsWhenSameValue() {
            UUID value = UUID.randomUUID();
            OrderId a = new OrderId(value);
            OrderId b = new OrderId(value);
            assertThat(a).isEqualTo(b);
            assertThat(a.hashCode()).isEqualTo(b.hashCode());
        }

        @Test
        void notEqualsWhenDifferentValue() {
            OrderId a = new OrderId(UUID.randomUUID());
            OrderId b = new OrderId(UUID.randomUUID());
            assertThat(a).isNotEqualTo(b);
        }
    }
}
