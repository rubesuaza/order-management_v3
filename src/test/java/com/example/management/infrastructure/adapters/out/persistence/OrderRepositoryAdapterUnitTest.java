package com.example.management.infrastructure.adapters.out.persistence;

import com.example.management.domain.model.aggregate.Order;
import com.example.management.domain.model.valueobject.Money;
import com.example.management.domain.model.valueobject.OrderId;
import com.example.management.infrastructure.persistence.entity.OrderEntity;
import com.example.management.infrastructure.persistence.entity.OrderItemEntity;
import com.example.management.infrastructure.persistence.repository.OrderJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit test for OrderRepositoryAdapter with mocked JPA repository.
 */
@ExtendWith(MockitoExtension.class)
class OrderRepositoryAdapterUnitTest {

    @Mock
    private OrderJpaRepository jpaRepository;

    private OrderRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new OrderRepositoryAdapter(jpaRepository);
    }

    @Test
    void save_delegatesToJpaRepositoryAndReturnsDomainOrder() {
        OrderId orderId = OrderId.generate();
        Order order = new Order(orderId);
        order.addItem("PROD-001", 2, Money.usd(BigDecimal.valueOf(15.00)));

        when(jpaRepository.save(any(OrderEntity.class))).thenAnswer(invocation -> {
            OrderEntity entity = invocation.getArgument(0);
            return entity;
        });

        Order saved = adapter.save(order);

        verify(jpaRepository).save(any(OrderEntity.class));
        assertThat(saved.getId()).isEqualTo(order.getId());
        assertThat(saved.getItems()).hasSize(1);
        assertThat(saved.getTotalAmount().getAmount()).isEqualByComparingTo(BigDecimal.valueOf(30.00));
    }

    @Test
    void findById_whenExists_returnsOrder() {
        UUID uuid = UUID.randomUUID();
        OrderId orderId = OrderId.of(uuid);
        OrderEntity entity = createOrderEntity(uuid, OrderEntity.OrderStatusEntity.PENDING);

        when(jpaRepository.findById(uuid)).thenReturn(Optional.of(entity));

        var result = adapter.findById(orderId);

        assertThat(result).isPresent();
        assertThat(result.get().getId().getValue()).isEqualTo(uuid);
        assertThat(result.get().getItems()).hasSize(1);
        assertThat(result.get().getTotalAmount().getAmount()).isEqualByComparingTo(BigDecimal.valueOf(30.00));
    }

    @Test
    void findById_whenNotExists_returnsEmpty() {
        OrderId orderId = OrderId.generate();

        when(jpaRepository.findById(orderId.getValue())).thenReturn(Optional.empty());

        var result = adapter.findById(orderId);

        assertThat(result).isEmpty();
    }

    @Test
    void existsById_delegatesToJpaRepository() {
        OrderId orderId = OrderId.generate();

        when(jpaRepository.existsById(orderId.getValue())).thenReturn(true);

        boolean exists = adapter.existsById(orderId);

        verify(jpaRepository).existsById(orderId.getValue());
        assertThat(exists).isTrue();
    }

    @Test
    void existsById_whenNotExists_returnsFalse() {
        OrderId orderId = OrderId.generate();

        when(jpaRepository.existsById(orderId.getValue())).thenReturn(false);

        boolean exists = adapter.existsById(orderId);

        assertThat(exists).isFalse();
    }

    private OrderEntity createOrderEntity(UUID id, OrderEntity.OrderStatusEntity status) {
        OrderEntity entity = new OrderEntity(id, status, BigDecimal.valueOf(30.00), "USD");
        OrderItemEntity itemEntity = new OrderItemEntity(
                entity, 1, "PROD-001", 2, BigDecimal.valueOf(15.00), "USD");
        entity.getItems().add(itemEntity);
        return entity;
    }
}
