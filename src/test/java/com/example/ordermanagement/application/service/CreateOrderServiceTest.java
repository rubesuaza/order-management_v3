package com.example.ordermanagement.application.service;

import com.example.ordermanagement.application.ports.out.OrderRepository;
import com.example.ordermanagement.domain.exception.InvalidOrderStateException;
import com.example.ordermanagement.domain.model.aggregate.Order;
import com.example.ordermanagement.domain.model.entity.OrderItem;
import com.example.ordermanagement.domain.model.valueobject.Money;
import com.example.ordermanagement.domain.model.valueobject.OrderId;
import com.example.ordermanagement.domain.model.valueobject.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateOrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    private CreateOrderService createOrderService;

    private static final Money FIVE_USD = new Money(new BigDecimal("5.00"), "USD");

    @BeforeEach
    void setUp() {
        createOrderService = new CreateOrderService(orderRepository);
    }

    @Test
    void shouldCreateAndSaveOrderWithItems() {
        OrderItem item1 = new OrderItem(UUID.randomUUID(), "prod-1", 2, FIVE_USD);
        OrderItem item2 = new OrderItem(UUID.randomUUID(), "prod-2", 1, FIVE_USD);
        Order expectedOrder = Order.create(OrderId.generate());
        expectedOrder.addItem(item1);
        expectedOrder.addItem(item2);
        expectedOrder.place();

        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        Order result = createOrderService.createOrder(List.of(item1, item2));

        assertNotNull(result);
        assertEquals(OrderStatus.PENDING, result.getStatus());
        assertEquals(2, result.getItems().size());
        assertEquals(new Money(new BigDecimal("15.00"), "USD"), result.getTotalAmount());

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository, times(1)).save(orderCaptor.capture());
        assertEquals(OrderStatus.PENDING, orderCaptor.getValue().getStatus());
    }

    @Test
    void shouldThrowWhenTotalLessThanMinimum() {
        OrderItem item = new OrderItem(UUID.randomUUID(), "prod-1", 1, FIVE_USD);

        assertThrows(InvalidOrderStateException.class, () ->
                createOrderService.createOrder(List.of(item)));

        verify(orderRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenItemsListIsEmpty() {
        assertThrows(InvalidOrderStateException.class, () ->
                createOrderService.createOrder(List.of()));

        verify(orderRepository, never()).save(any());
    }
}
