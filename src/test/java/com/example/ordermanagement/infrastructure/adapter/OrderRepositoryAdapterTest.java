package com.example.ordermanagement.infrastructure.adapter;

import com.example.ordermanagement.application.ports.out.OrderRepository;
import com.example.ordermanagement.domain.model.aggregate.Order;
import com.example.ordermanagement.domain.model.entity.OrderItem;
import com.example.ordermanagement.domain.model.valueobject.Money;
import com.example.ordermanagement.domain.model.valueobject.OrderId;
import com.example.ordermanagement.domain.model.valueobject.OrderStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(OrderRepositoryAdapter.class)
@ActiveProfiles("test")
class OrderRepositoryAdapterTest {

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void shouldSaveAndFindOrder() {
        OrderId orderId = OrderId.generate();
        Order order = Order.create(orderId);
        order.addItem(new OrderItem(UUID.randomUUID(), "prod-1", 2, new Money(new BigDecimal("5.00"), "USD")));
        order.addItem(new OrderItem(UUID.randomUUID(), "prod-2", 1, new Money(new BigDecimal("10.00"), "USD")));
        order.place();

        Order saved = orderRepository.save(order);

        assertNotNull(saved);
        assertEquals(orderId.getValue(), saved.getId().getValue());
        assertEquals(OrderStatus.PENDING, saved.getStatus());
        assertEquals(2, saved.getItems().size());
        assertEquals(new Money(new BigDecimal("20.00"), "USD"), saved.getTotalAmount());

        Optional<Order> found = orderRepository.findById(orderId);
        assertTrue(found.isPresent());
        assertEquals(orderId.getValue(), found.get().getId().getValue());
        assertEquals(2, found.get().getItems().size());
    }

    @Test
    void shouldReturnEmptyWhenOrderNotFound() {
        Optional<Order> found = orderRepository.findById(OrderId.generate());
        assertTrue(found.isEmpty());
    }

    @Test
    void shouldPersistOrderWithPaidStatus() {
        Order order = Order.create(OrderId.generate());
        order.addItem(new OrderItem(UUID.randomUUID(), "prod-1", 2, new Money(new BigDecimal("5.00"), "USD")));
        order.place();
        order.pay();

        Order saved = orderRepository.save(order);
        assertEquals(OrderStatus.PAID, saved.getStatus());

        Optional<Order> found = orderRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals(OrderStatus.PAID, found.get().getStatus());
    }
}
