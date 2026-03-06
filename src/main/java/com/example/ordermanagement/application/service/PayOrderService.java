package com.example.ordermanagement.application.service;

import com.example.ordermanagement.application.ports.in.OrderResult;
import com.example.ordermanagement.application.ports.in.PayOrderUseCase;
import com.example.ordermanagement.application.ports.out.OrderRepository;
import com.example.ordermanagement.domain.model.aggregate.Order;
import com.example.ordermanagement.domain.model.valueobject.OrderId;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Application service implementing PayOrderUseCase.
 */
@Service
public class PayOrderService implements PayOrderUseCase {

    private final OrderRepository orderRepository;

    public PayOrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public Optional<OrderResult> payOrder(OrderId orderId) {
        return orderRepository.findById(orderId)
                .map(order -> {
                    order.pay();
                    Order saved = orderRepository.save(order);
                    return new OrderResult(saved.getId(), saved.getStatus().name());
                });
    }
}
