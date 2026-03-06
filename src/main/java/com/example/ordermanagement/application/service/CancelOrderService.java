package com.example.ordermanagement.application.service;

import com.example.ordermanagement.application.ports.in.CancelOrderUseCase;
import com.example.ordermanagement.application.ports.in.OrderResult;
import com.example.ordermanagement.application.ports.out.OrderRepository;
import com.example.ordermanagement.domain.model.aggregate.Order;
import com.example.ordermanagement.domain.model.valueobject.OrderId;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Application service implementing CancelOrderUseCase.
 */
@Service
public class CancelOrderService implements CancelOrderUseCase {

    private final OrderRepository orderRepository;

    public CancelOrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public Optional<OrderResult> cancelOrder(OrderId orderId) {
        return orderRepository.findById(orderId)
                .map(order -> {
                    order.cancel();
                    Order saved = orderRepository.save(order);
                    return new OrderResult(saved.getId(), saved.getStatus().name());
                });
    }
}
