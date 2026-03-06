package com.example.management.infrastructure.persistence.adapter;

import com.example.management.application.ports.out.OrderRepository;
import com.example.management.domain.model.aggregate.Order;
import com.example.management.domain.model.aggregate.OrderId;
import com.example.management.domain.model.entity.OrderItem;
import com.example.management.domain.model.valueobject.Money;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(OrderRepositoryAdapter.class)
@EntityScan(basePackages = "com.example.management.infrastructure.persistence.entity")
@EnableJpaRepositories(basePackages = "com.example.management.infrastructure.persistence.repository")
@ActiveProfiles("test")
class OrderRepositoryAdapterTest {

    @Autowired
    private OrderRepository repository;

    @Test
    void save_and_findById_roundtrips_order() {
        OrderId id = OrderId.generate();
        Order order = new Order(id);
        order.addItem(new OrderItem("PROD-001", 2, Money.usd(new BigDecimal("15.00"))));
        order.addItem(new OrderItem("PROD-002", 1, Money.usd(new BigDecimal("25.00"))));

        Order saved = repository.save(order);

        assertThat(saved.getId()).isEqualTo(id);
        assertThat(saved.getItems()).hasSize(2);
        assertThat(saved.getTotalAmount().getAmount()).isEqualByComparingTo(new BigDecimal("55.00"));

        Optional<Order> found = repository.findById(id);
        assertThat(found).isPresent();
        assertThat(found.get().getId().getValue()).isEqualTo(id.getValue());
        assertThat(found.get().getItems()).hasSize(2);
        assertThat(found.get().getStatus()).isEqualTo(saved.getStatus());
    }

    @Test
    void existsById_returnsTrue_whenOrderExists() {
        OrderId id = OrderId.generate();
        Order order = new Order(id);
        order.addItem(new OrderItem("PROD-001", 1, Money.usd(new BigDecimal("20.00"))));
        repository.save(order);

        assertThat(repository.existsById(id)).isTrue();
    }

    @Test
    void existsById_returnsFalse_whenOrderDoesNotExist() {
        OrderId id = OrderId.generate();
        assertThat(repository.existsById(id)).isFalse();
    }

    @Test
    void findById_returnsEmpty_whenOrderDoesNotExist() {
        OrderId id = OrderId.generate();
        assertThat(repository.findById(id)).isEmpty();
    }
}
