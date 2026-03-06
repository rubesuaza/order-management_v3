package com.example.ordermanagement.application.service;

import com.example.ordermanagement.application.ports.in.OrderResult;
import com.example.ordermanagement.application.ports.out.OrderRepository;
import com.example.ordermanagement.domain.model.aggregate.Order;
import com.example.ordermanagement.domain.model.entity.OrderItem;
import com.example.ordermanagement.domain.model.valueobject.Money;
import com.example.ordermanagement.domain.model.valueobject.OrderId;
import com.example.ordermanagement.domain.model.valueobject.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PayOrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    private PayOrderService payOrderService;

    private static final Money FIVE_USD = new Money(new BigDecimal("5.00"), "USD");

    @BeforeEach
    void setUp() {
        payOrderService = new PayOrderService(orderRepository);
    }

    @Test
    void shouldPayOrderWhenFoundAndPending() {
        OrderId orderId = OrderId.generate();
        Order order = Order.create(orderId);
        order.addItem(new OrderItem(UUID.randomUUID(), "prod-1", 2, FIVE_USD));
        order.place();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        Optional<OrderResult> result = payOrderService.payOrder(orderId);

        assertTrue(result.isPresent());
        assertEquals(orderId.getValue(), result.get().orderId().getValue());
        assertEquals(OrderStatus.PAID.name(), result.get().status());

        verify(orderRepository).findById(orderId);
        verify(orderRepository).save(order);
    }

    @Test
    void shouldReturnEmptyWhenOrderNotFound() {
        OrderId orderId = OrderId.generate();
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        Optional<OrderResult> result = payOrderService.payOrder(orderId);

        assertTrue(result.isEmpty());
        verify(orderRepository).findById(orderId);
        verify(orderRepository, never()).save(any());
    }
}
