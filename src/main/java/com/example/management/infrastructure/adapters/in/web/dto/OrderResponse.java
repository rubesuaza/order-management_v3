package com.example.management.infrastructure.adapters.in.web.dto;

import com.example.management.domain.model.Money;
import com.example.management.domain.model.Order;
import com.example.management.domain.model.OrderItem;
import com.example.management.domain.model.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * REST response for an order.
 */
public record OrderResponse(
        UUID id,
        UUID customerId,
        LocalDateTime createdAt,
        String status,
        List<OrderItemResponse> items,
        BigDecimal totalAmount,
        String totalCurrency
) {
    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getCustomerId(),
                order.getCreatedAt(),
                order.getStatus().name(),
                order.getItems().stream().map(OrderItemResponse::from).toList(),
                order.getTotalAmount().getAmount(),
                order.getTotalAmount().getCurrency()
        );
    }

    public record OrderItemResponse(
            UUID productId,
            int quantity,
            BigDecimal unitPriceAmount,
            String unitPriceCurrency,
            BigDecimal lineTotalAmount,
            String lineTotalCurrency
    ) {
        static OrderItemResponse from(OrderItem item) {
            Money unit = item.getUnitPrice();
            Money line = item.getLineTotal();
            return new OrderItemResponse(
                    item.getProductId(),
                    item.getQuantity(),
                    unit.getAmount(),
                    unit.getCurrency(),
                    line.getAmount(),
                    line.getCurrency()
            );
        }
    }
}
