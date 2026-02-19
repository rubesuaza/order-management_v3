package com.example.order_management.infrastructure.adapters.in.web.mapper;

import com.example.order_management.domain.model.Order;
import com.example.order_management.domain.model.OrderItem;
import com.example.order_management.infrastructure.adapters.in.web.dto.CreateOrderResponse;
import com.example.order_management.infrastructure.adapters.in.web.dto.OrderResponse;
import com.example.order_management.infrastructure.adapters.in.web.dto.PayOrderResponse;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Mapper for converting between Domain Order entities and DTOs.
 */
@Component
public class OrderDtoMapper {

    public CreateOrderResponse toCreateResponse(Order order) {
        return new CreateOrderResponse(
                order.getId(),
                order.getStatus().name(),
                order.getTotalAmount().getAmount(),
                order.getCreatedAt()
        );
    }

    public OrderResponse toOrderResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getCustomerId(),
                order.getStatus().name(),
                order.getItems().stream()
                        .map(item -> new OrderResponse.OrderItemResponse(
                                item.getProductId(),
                                item.getQuantity(),
                                item.getUnitPrice().getAmount()
                        ))
                        .collect(Collectors.toList()),
                order.getTotalAmount().getAmount(),
                order.getTotalAmount().getCurrency(),
                order.getCreatedAt()
        );
    }

    public PayOrderResponse toPayResponse(Order order) {
        return new PayOrderResponse(
                order.getId(),
                order.getStatus().name()
        );
    }
}
