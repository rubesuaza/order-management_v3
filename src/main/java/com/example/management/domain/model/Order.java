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
        Money sum = items.get(0).getLineTotal();
        for (int i = 1; i < items.size(); i++) {
            sum = sum.add(items.get(i).getLineTotal());
        }
        return sum;
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
        if (status != OrderStatus.PAID) {
            throw new InvalidOrderStateException("Order can only be shipped when PAID");
        }
        status = OrderStatus.SHIPPED;
    }

    /**
     * Transition to DELIVERED. Only allowed when SHIPPED.
     */
    public void deliver() {
        if (status != OrderStatus.SHIPPED) {
            throw new InvalidOrderStateException("Order can only be delivered when SHIPPED");
        }
        status = OrderStatus.DELIVERED;
    }

    /**
     * Transition to CANCELLED. Only allowed when PENDING or PAID.
     */
    public void cancel() {
        if (status == OrderStatus.SHIPPED || status == OrderStatus.DELIVERED) {
            throw new InvalidOrderStateException(
                    "Order cannot be cancelled when already shipped or delivered");
        }
        status = OrderStatus.CANCELLED;
    }
}
