package com.example.ordermanagement.application.service;

import com.example.ordermanagement.application.ports.in.CreateOrderUseCase;
import com.example.ordermanagement.application.ports.out.OrderRepository;
import com.example.ordermanagement.domain.model.aggregate.Order;
import com.example.ordermanagement.domain.model.entity.OrderItem;
import com.example.ordermanagement.domain.model.valueobject.OrderId;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Application service implementing CreateOrderUseCase.
 */
@Service
public class CreateOrderService implements CreateOrderUseCase {

    private final OrderRepository orderRepository;

    public CreateOrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public Order createOrder(List<OrderItem> items) {
        Order order = Order.create(OrderId.generate());
        for (OrderItem item : items) {
            order.addItem(item);
        }
        order.place();
        return orderRepository.save(order);
    }
}
