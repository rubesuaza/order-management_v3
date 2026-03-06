package com.example.management.infrastructure.adapters.out.persistence;

import com.example.management.application.ports.out.OrderRepository;
import com.example.management.domain.model.aggregate.Order;
import com.example.management.domain.model.valueobject.Money;
import com.example.management.domain.model.valueobject.OrderId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test for OrderRepositoryAdapter.
 */
@SpringBootTest(classes = com.example.management.ManagementApplication.class)
@ActiveProfiles("test")
class OrderRepositoryAdapterTest {

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void shouldSaveAndFindOrder() {
        Order order = new Order(OrderId.generate());
        order.addItem("PROD-001", 2, Money.usd(BigDecimal.valueOf(15.00)));
        order.addItem("PROD-002", 1, Money.usd(BigDecimal.valueOf(25.50)));

        Order saved = orderRepository.save(order);

        assertThat(saved.getId()).isEqualTo(order.getId());
        assertThat(saved.getItems()).hasSize(2);
        assertThat(saved.getTotalAmount().getAmount()).isEqualByComparingTo(BigDecimal.valueOf(55.50));

        Order found = orderRepository.findById(order.getId()).orElseThrow();
        assertThat(found.getId()).isEqualTo(order.getId());
        assertThat(found.getItems()).hasSize(2);
        assertThat(found.getTotalAmount().getAmount()).isEqualByComparingTo(BigDecimal.valueOf(55.50));
    }

    @Test
    void shouldReturnEmptyWhenOrderNotFound() {
        OrderId nonExistent = OrderId.generate();
        assertThat(orderRepository.findById(nonExistent)).isEmpty();
        assertThat(orderRepository.existsById(nonExistent)).isFalse();
    }

    @Test
    void shouldPersistOrderStatus() {
        Order order = new Order(OrderId.generate());
        order.addItem("PROD-001", 1, Money.usd(BigDecimal.valueOf(20.00)));
        order.place();
        order.pay();

        orderRepository.save(order);

        Order found = orderRepository.findById(order.getId()).orElseThrow();
        assertThat(found.getStatus()).isEqualTo(com.example.management.domain.model.valueobject.OrderStatus.PAID);
    }
}
