package com.example.order_management.infrastructure.adapters.out;

import com.example.order_management.application.ports.out.OrderRepository;
import com.example.order_management.domain.model.Money;
import com.example.order_management.domain.model.Order;
import com.example.order_management.domain.model.OrderItem;
import com.example.order_management.domain.model.OrderStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test for OrderRepositoryAdapter.
 * Tests persistence operations using an in-memory database.
 */
@DataJpaTest
@ActiveProfiles("dev")
@Import({OrderRepositoryAdapter.class, com.example.order_management.infrastructure.persistence.mapper.OrderMapper.class})
class OrderRepositoryAdapterTest {

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void shouldSaveAndRetrieveOrder() {
        // Given
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        OrderItem item = new OrderItem(productId, 2, new Money(new BigDecimal("15.50")));
        Order order = new Order(customerId, List.of(item));

        // When
        Order savedOrder = orderRepository.save(order);
        Optional<Order> retrievedOrder = orderRepository.findById(savedOrder.getId());

        // Then
        assertThat(retrievedOrder).isPresent();
        assertThat(retrievedOrder.get().getId()).isEqualTo(savedOrder.getId());
        assertThat(retrievedOrder.get().getCustomerId()).isEqualTo(customerId);
        assertThat(retrievedOrder.get().getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(retrievedOrder.get().getItems()).hasSize(1);
        assertThat(retrievedOrder.get().getTotalAmount().getAmount())
                .isEqualByComparingTo(new BigDecimal("31.00"));
    }

    @Test
    void shouldReturnEmptyWhenOrderNotFound() {
        // When
        Optional<Order> order = orderRepository.findById(UUID.randomUUID());

        // Then
        assertThat(order).isEmpty();
    }

    @Test
    void shouldUpdateOrderStatus() {
        // Given
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        OrderItem item = new OrderItem(productId, 1, new Money(new BigDecimal("20.00")));
        Order order = new Order(customerId, List.of(item));
        Order savedOrder = orderRepository.save(order);

        // When - update status
        savedOrder.markAsPaid();
        Order updatedOrder = orderRepository.save(savedOrder);

        // Then
        Optional<Order> retrievedOrder = orderRepository.findById(updatedOrder.getId());
        assertThat(retrievedOrder).isPresent();
        assertThat(retrievedOrder.get().getStatus()).isEqualTo(OrderStatus.PAID);
    }
}
