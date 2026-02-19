package com.example.order_management.domain.model;

import com.example.order_management.domain.exception.InvalidOrderStateException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Aggregate Root representing an Order.
 * Encapsulates business rules and invariants for order management.
 */
public class Order {
    private static final BigDecimal MINIMUM_ORDER_VALUE = new BigDecimal("10.00");
    
    private final UUID id;
    private OrderStatus status;
    private final LocalDateTime createdAt;
    private final List<OrderItem> items;
    private final UUID customerId;
    private Money totalAmount;

    public Order(UUID customerId, List<OrderItem> items) {
        this(UUID.randomUUID(), customerId, items, OrderStatus.PENDING, 
             LocalDateTime.now(), null);
        this.totalAmount = calculateTotalAmount();
    }

    /**
     * Public constructor for reconstructing Order from persistence layer.
     * Allows infrastructure layer to reconstruct orders without using reflection.
     * This constructor should primarily be used by infrastructure adapters.
     * 
     * @param id The order ID from database
     * @param customerId The customer ID
     * @param items The order items
     * @param status The order status
     * @param createdAt The creation timestamp
     * @param totalAmount The total amount (if null, will be calculated)
     */
    public Order(UUID id, UUID customerId, List<OrderItem> items, OrderStatus status, 
          LocalDateTime createdAt, Money totalAmount) {
        if (id == null) {
            throw new IllegalArgumentException("Order ID cannot be null");
        }
        if (customerId == null) {
            throw new IllegalArgumentException("Customer ID cannot be null");
        }
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Order must have at least one item");
        }
        if (status == null) {
            throw new IllegalArgumentException("Order status cannot be null");
        }
        if (createdAt == null) {
            throw new IllegalArgumentException("Created at cannot be null");
        }
        
        this.id = id;
        this.customerId = customerId;
        this.items = new ArrayList<>(items);
        this.status = status;
        this.createdAt = createdAt;
        this.totalAmount = totalAmount != null ? totalAmount : calculateTotalAmount();
    }

    public UUID getId() {
        return id;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public Money getTotalAmount() {
        return totalAmount;
    }

    /**
     * Marks the order as paid.
     * Business rule: Order cannot be paid if total amount is less than 10.00 USD.
     */
    public void markAsPaid() {
        if (!meetsMinimumOrderValue()) {
            throw new InvalidOrderStateException(
                    String.format("Minimum order value is 10.00 USD. Current total: %s", totalAmount)
            );
        }
        this.status = OrderStatus.PAID;
    }

    /**
     * Checks if the order meets the minimum order value requirement.
     * Extracted for better readability and testability.
     */
    private boolean meetsMinimumOrderValue() {
        Money minimumOrderValue = new Money(MINIMUM_ORDER_VALUE);
        return totalAmount.compareTo(minimumOrderValue) >= 0;
    }

    /**
     * Marks the order as shipped.
     * Business rule: Order can only be shipped if it is in PAID status.
     */
    public void markAsShipped() {
        if (status != OrderStatus.PAID) {
            throw new InvalidOrderStateException(
                    String.format("Order can only be shipped if it is PAID. Current status: %s", status)
            );
        }
        this.status = OrderStatus.SHIPPED;
    }

    /**
     * Marks the order as delivered.
     */
    public void markAsDelivered() {
        if (status != OrderStatus.SHIPPED) {
            throw new InvalidOrderStateException(
                    String.format("Order can only be delivered if it is SHIPPED. Current status: %s", status)
            );
        }
        this.status = OrderStatus.DELIVERED;
    }

    /**
     * Cancels the order.
     * Business rule: Order can only be cancelled if it is PENDING or PAID.
     */
    public void cancel() {
        if (isShippedOrDelivered()) {
            throw new InvalidOrderStateException(
                    String.format("Order with status %s cannot be cancelled", status)
            );
        }
        this.status = OrderStatus.CANCELLED;
    }


    /**
     * Checks if the order is in a shipped or delivered state.
     * Extracted for better readability and maintainability.
     */
    private boolean isShippedOrDelivered() {
        return status == OrderStatus.SHIPPED || status == OrderStatus.DELIVERED;
    }

    /**
     * Calculates the total amount by summing all item subtotals.
     * Uses Java Streams for better readability and alignment with Java best practices.
     */
    private Money calculateTotalAmount() {
        return items.stream()
                .map(OrderItem::calculateSubtotal)
                .reduce(new Money(BigDecimal.ZERO), Money::add);
    }

    @Override
    public String toString() {
        return String.format("Order{id=%s, status=%s, customerId=%s, totalAmount=%s, items=%d}",
                id, status, customerId, totalAmount, items.size());
    }
}
