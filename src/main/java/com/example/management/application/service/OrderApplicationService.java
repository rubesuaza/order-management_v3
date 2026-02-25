package com.example.management.application.service;

import com.example.management.application.port.in.CreateOrderCommand;
import com.example.management.application.port.in.CreateOrderUseCase;
import com.example.management.application.port.in.GetOrderUseCase;
import com.example.management.application.port.in.OrderItemRequest;
import com.example.management.application.port.in.PayOrderUseCase;
import com.example.management.domain.port.out.OrderPersistencePort;
import com.example.management.domain.model.Order;
import com.example.management.domain.model.OrderId;
import com.example.management.domain.model.OrderItem;
import com.example.management.domain.valueobject.Money;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Application service implementing order use cases. Depends only on domain and ports.
 */
@Service
public class OrderApplicationService implements CreateOrderUseCase, GetOrderUseCase, PayOrderUseCase {

    private static final String DEFAULT_CURRENCY = "USD";

    private final OrderPersistencePort orderPersistence;

    public OrderApplicationService(OrderPersistencePort orderPersistence) {
        this.orderPersistence = orderPersistence;
    }

    @Override
    @Transactional
    public Order create(CreateOrderCommand command) {
        if (command == null || command.items() == null || command.items().isEmpty()) {
            throw new IllegalArgumentException("Order must have at least one item");
        }
        List<OrderItem> domainItems = command.items().stream()
                .map(i -> new OrderItem(i.productId(), i.quantity(), new Money(i.unitPrice(), DEFAULT_CURRENCY)))
                .toList();
        Order order = new Order(UUID.randomUUID(), command.customerId(), domainItems);
        return orderPersistence.save(order);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Order> getById(OrderId orderId) {
        return orderPersistence.findById(orderId);
    }

    @Override
    @Transactional
    public Order pay(OrderId orderId) {
        Order order = orderPersistence.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId.getValue()));
        order.markAsPaid();
        return orderPersistence.save(order);
    }
}
