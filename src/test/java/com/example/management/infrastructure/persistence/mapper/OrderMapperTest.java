package com.example.management.infrastructure.persistence.mapper;

import com.example.management.domain.OrderStatus;
import com.example.management.domain.model.Order;
import com.example.management.domain.model.OrderItem;
import com.example.management.domain.valueobject.Money;
import com.example.management.infrastructure.persistence.entity.OrderEntity;
import com.example.management.infrastructure.persistence.entity.OrderItemEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("OrderMapper")
class OrderMapperTest {

    private OrderMapper mapper;

    private static final UUID ORDER_ID = UUID.randomUUID();
    private static final UUID CUSTOMER_ID = UUID.randomUUID();
    private static final UUID PRODUCT_ID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        mapper = new OrderMapper();
    }

    @Nested
    @DisplayName("toEntity")
    class ToEntity {
        @Test
        void mapsOrderToEntityWithItems() {
            OrderItem item = new OrderItem(PRODUCT_ID, 2, new Money(new BigDecimal("5.00")));
            Order order = Order.reconstitute(
                    ORDER_ID, CUSTOMER_ID, LocalDateTime.now(),
                    List.of(item), OrderStatus.PENDING);

            OrderEntity entity = mapper.toEntity(order);

            assertThat(entity.getId()).isEqualTo(ORDER_ID);
            assertThat(entity.getCustomerId()).isEqualTo(CUSTOMER_ID);
            assertThat(entity.getStatus()).isEqualTo("PENDING");
            assertThat(entity.getTotalAmount()).isEqualByComparingTo("10.00");
            assertThat(entity.getCurrency()).isEqualTo("USD");
            assertThat(entity.getItems()).hasSize(1);
            OrderItemEntity itemEntity = entity.getItems().get(0);
            assertThat(itemEntity.getProductId()).isEqualTo(PRODUCT_ID);
            assertThat(itemEntity.getQuantity()).isEqualTo(2);
            assertThat(itemEntity.getUnitPrice()).isEqualByComparingTo("5.00");
        }
    }

    @Nested
    @DisplayName("toDomain")
    class ToDomain {
        @Test
        void mapsEntityToDomainOrder() {
            OrderEntity entity = new OrderEntity(
                    ORDER_ID, CUSTOMER_ID, "PAID",
                    new BigDecimal("25.00"), "USD", LocalDateTime.now());
            OrderItemEntity itemEntity = new OrderItemEntity(
                    UUID.randomUUID(), PRODUCT_ID, 3, new BigDecimal("5.00"), "USD");
            entity.setItems(List.of(itemEntity));

            Order order = mapper.toDomain(entity);

            assertThat(order.getId().getValue()).isEqualTo(ORDER_ID);
            assertThat(order.getCustomerId()).isEqualTo(CUSTOMER_ID);
            assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);
            // Total is computed from items (3 * 5.00 = 15.00), not from entity total
            assertThat(order.getTotalAmount().getAmount()).isEqualByComparingTo("15.00");
            assertThat(order.getItems()).hasSize(1);
            OrderItem domainItem = order.getItems().get(0);
            assertThat(domainItem.getProductId()).isEqualTo(PRODUCT_ID);
            assertThat(domainItem.getQuantity()).isEqualTo(3);
            assertThat(domainItem.getUnitPrice().getAmount()).isEqualByComparingTo("5.00");
        }
    }

    @Nested
    @DisplayName("round-trip")
    class RoundTrip {
        @Test
        void domainToEntityToDomainPreservesData() {
            OrderItem item = new OrderItem(PRODUCT_ID, 1, new Money(new BigDecimal("12.50")));
            LocalDateTime createdAt = LocalDateTime.of(2025, 2, 1, 10, 0);
            Order order = Order.reconstitute(
                    ORDER_ID, CUSTOMER_ID, createdAt,
                    List.of(item), OrderStatus.SHIPPED);

            OrderEntity entity = mapper.toEntity(order);
            Order restored = mapper.toDomain(entity);

            assertThat(restored.getId().getValue()).isEqualTo(order.getId().getValue());
            assertThat(restored.getCustomerId()).isEqualTo(order.getCustomerId());
            assertThat(restored.getStatus()).isEqualTo(order.getStatus());
            assertThat(restored.getTotalAmount().getAmount()).isEqualByComparingTo(order.getTotalAmount().getAmount());
            assertThat(restored.getItems()).hasSize(1);
            assertThat(restored.getItems().get(0).getQuantity()).isEqualTo(1);
            assertThat(restored.getItems().get(0).getUnitPrice().getAmount()).isEqualByComparingTo("12.50");
        }
    }
}
