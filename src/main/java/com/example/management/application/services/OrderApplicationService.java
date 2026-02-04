package com.example.management.application.services;

import com.example.management.application.ports.in.OrderManagementUseCase;
import com.example.management.application.ports.out.OrderRepository;
import com.example.management.domain.model.Money;
import com.example.management.domain.model.Order;
import com.example.management.domain.model.OrderItem;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Application service implementing order management use cases.
 * Depends only on output ports (OrderRepository); used by input adapters.
 */
@Service
public class OrderApplicationService implements OrderManagementUseCase {

    private final OrderRepository orderRepository;

    public OrderApplicationService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public Order createOrder(UUID customerId, List<OrderItemCommand> items) {
        List<OrderItem> domainItems = items.stream()
                .map(cmd -> new OrderItem(
                        cmd.productId(),
                        cmd.quantity(),
                        new Money(cmd.unitPriceAmount(), cmd.unitPriceCurrency() != null ? cmd.unitPriceCurrency() : "USD")))
                .toList();
        Order order = new Order(UUID.randomUUID(), customerId, domainItems, LocalDateTime.now());
        return orderRepository.save(order);
    }

    @Override
    public Optional<Order> getOrderById(UUID orderId) {
        return orderRepository.findById(orderId);
    }

    @Override
    public List<Order> listOrders() {
        return orderRepository.findAll();
    }

    @Override
    public void markOrderAsPaid(UUID orderId) {
        Order order = getOrderOrThrow(orderId);
        order.markAsPaid();
        orderRepository.save(order);
    }

    @Override
    public void shipOrder(UUID orderId) {
        Order order = getOrderOrThrow(orderId);
        order.ship();
        orderRepository.save(order);
    }

    @Override
    public void deliverOrder(UUID orderId) {
        Order order = getOrderOrThrow(orderId);
        order.deliver();
        orderRepository.save(order);
    }

    @Override
    public void cancelOrder(UUID orderId) {
        Order order = getOrderOrThrow(orderId);
        order.cancel();
        orderRepository.save(order);
    }

    private Order getOrderOrThrow(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
    }
}
