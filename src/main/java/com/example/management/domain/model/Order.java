package com.example.management.domain.model;

import com.example.management.domain.OrderStatus;
import com.example.management.domain.exception.InvalidOrderStateException;
import com.example.management.domain.valueobject.Money;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Order aggregate root. Must have at least one item; total is sum of line totals;
 * minimum 10.00 USD to mark as PAID; state transitions enforced.
 */
public class Order {

    private static final java.math.BigDecimal MINIMUM_ORDER_AMOUNT = new java.math.BigDecimal("10.00");

    private final OrderId id;
    private final UUID customerId;
    private final LocalDateTime createdAt;
    private final List<OrderItem> items;
    private final Money totalAmount;
    private OrderStatus status;

    public Order(UUID id, UUID customerId, List<OrderItem> items) {
        this(id, customerId, items, LocalDateTime.now(), OrderStatus.PENDING);
    }

    /**
     * Reconstitutes an order from persistence (known state and creation time).
     */
    public static Order reconstitute(UUID id, UUID customerId, LocalDateTime createdAt, List<OrderItem> items, OrderStatus status) {
        return new Order(id, customerId, items, createdAt, status);
    }

    private Order(UUID id, UUID customerId, List<OrderItem> items, LocalDateTime createdAt, OrderStatus status) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Order must have at least one item");
        }
        this.id = new OrderId(id);
        this.customerId = Objects.requireNonNull(customerId, "customerId");
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        this.items = new ArrayList<>(items);
        this.totalAmount = computeTotal(items);
        this.status = status != null ? status : OrderStatus.PENDING;
    }

    private static Money computeTotal(List<OrderItem> items) {
        Money total = new Money(java.math.BigDecimal.ZERO);
        for (OrderItem item : items) {
            total = total.add(item.getLineTotal());
        }
        return total;
    }

    public OrderId getId() {
        return id;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public Money getTotalAmount() {
        return totalAmount;
    }

    public OrderStatus getStatus() {
        return status;
    }

    /**
     * Order can only be marked PAID if status is PENDING and total >= 10.00 USD.
     */
    public void markAsPaid() {
        if (status != OrderStatus.PENDING) {
            throw new InvalidOrderStateException(
                    "Order can only be marked as PAID when status is PENDING. Current: " + status);
        }
        if (totalAmount.getAmount().compareTo(MINIMUM_ORDER_AMOUNT) < 0) {
            throw new InvalidOrderStateException(
                    "Order cannot be placed: total amount must be at least 10.00 USD. Current: " + totalAmount.getAmount());
        }
        this.status = OrderStatus.PAID;
    }

    /**
     * Order can only be SHIPPED when status is PAID.
     */
    public void ship() {
        if (status != OrderStatus.PAID) {
            throw new InvalidOrderStateException(
                    "Order can only be shipped when status is PAID. Current: " + status);
        }
        this.status = OrderStatus.SHIPPED;
    }

    /**
     * Order can only be CANCELLED when status is PENDING or PAID. SHIPPED cannot be cancelled.
     */
    public void cancel() {
        if (status == OrderStatus.SHIPPED || status == OrderStatus.DELIVERED) {
            throw new InvalidOrderStateException(
                    "Order cannot be cancelled when already SHIPPED or DELIVERED. Current: " + status);
        }
        this.status = OrderStatus.CANCELLED;
    }
}
