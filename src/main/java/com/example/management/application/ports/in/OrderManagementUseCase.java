package com.example.management.application.ports.in;

import com.example.management.domain.model.Order;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Input port for order management use cases.
 * Implemented by the application service; used by input adapters (e.g. REST controller).
 */
public interface OrderManagementUseCase {

    Order createOrder(UUID customerId, List<OrderItemCommand> items);

    Optional<Order> getOrderById(UUID orderId);

    List<Order> listOrders();

    void markOrderAsPaid(UUID orderId);

    void shipOrder(UUID orderId);

    void deliverOrder(UUID orderId);

    void cancelOrder(UUID orderId);

    /**
     * Command for a single line item when creating an order.
     */
    record OrderItemCommand(UUID productId, int quantity, java.math.BigDecimal unitPriceAmount, String unitPriceCurrency) {}
}
