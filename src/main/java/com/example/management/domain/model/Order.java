package com.example.management.domain.model;

import com.example.management.domain.exception.InvalidOrderStateException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Aggregate root for the order lifecycle.
 * Invariants: at least one item; total = sum of line totals; min 10 USD to mark as PAID;
 * valid state transitions for cancel/ship/deliver.
 */
public final class Order {

    private static final BigDecimal MINIMUM_ORDER_AMOUNT = new BigDecimal("10.00");

    private final UUID id;
    private final UUID customerId;
    private final LocalDateTime createdAt;
    private final List<OrderItem> items;
    private final Money totalAmount;
    private OrderStatus status;

    public Order(UUID id, UUID customerId, List<OrderItem> items, LocalDateTime createdAt) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Order must have at least one item");
        }
        this.id = Objects.requireNonNull(id, "id");
        this.customerId = Objects.requireNonNull(customerId, "customerId");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
        this.items = new ArrayList<>(items);
        this.totalAmount = calculateTotal(items);
        this.status = OrderStatus.PENDING;
    }

    private static Money calculateTotal(List<OrderItem> items) {
        return items.stream()
                .map(OrderItem::getLineTotal)
                .reduce(Money::add)
                .orElseThrow();
    }

    public UUID getId() {
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

    private boolean canTransitionToShipped() {
        return status == OrderStatus.PAID;
    }

    private boolean canTransitionToDelivered() {
        return status == OrderStatus.SHIPPED;
    }

    private boolean canBeCancelled() {
        return status != OrderStatus.SHIPPED && status != OrderStatus.DELIVERED;
    }

    /**
     * Transition to PAID. Fails if total is less than 10.00 USD.
     */
    public void markAsPaid() {
        if (status != OrderStatus.PENDING) {
            throw new InvalidOrderStateException("Order can only be paid when PENDING");
        }
        if (totalAmount.getAmount().compareTo(MINIMUM_ORDER_AMOUNT) < 0) {
            throw new InvalidOrderStateException(
                    "Order total must be at least 10.00 USD to be placed");
        }
        status = OrderStatus.PAID;
    }

    /**
     * Transition to SHIPPED. Only allowed when PAID.
     */
    public void ship() {
        if (!canTransitionToShipped()) {
            throw new InvalidOrderStateException("Order can only be shipped when PAID");
        }
        status = OrderStatus.SHIPPED;
    }

    /**
     * Transition to DELIVERED. Only allowed when SHIPPED.
     */
    public void deliver() {
        if (!canTransitionToDelivered()) {
            throw new InvalidOrderStateException("Order can only be delivered when SHIPPED");
        }
        status = OrderStatus.DELIVERED;
    }

    /**
     * Transition to CANCELLED. Only allowed when PENDING or PAID.
     */
    public void cancel() {
        if (!canBeCancelled()) {
            throw new InvalidOrderStateException(
                    "Order cannot be cancelled when already shipped or delivered");
        }
        status = OrderStatus.CANCELLED;
    }
}
