package com.example.order_management.domain.model;

import com.example.order_management.domain.exception.InvalidOrderStateException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Aggregate Root representing an Order.
 * Encapsulates business rules and invariants for order management.
 */
public class Order {
    private final UUID id;
    private OrderStatus status;
    private final LocalDateTime createdAt;
    private final List<OrderItem> items;
    private final UUID customerId;
    private Money totalAmount;

    public Order(UUID customerId, List<OrderItem> items) {
        if (customerId == null) {
            throw new IllegalArgumentException("Customer ID cannot be null");
        }
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Order must have at least one item");
        }
        
        this.id = UUID.randomUUID();
        this.customerId = customerId;
        this.items = new ArrayList<>(items);
        this.status = OrderStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.totalAmount = calculateTotalAmount();
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
        Money minimumOrderValue = new Money(new BigDecimal("10.00"));
        if (totalAmount.compareTo(minimumOrderValue) < 0) {
            throw new InvalidOrderStateException(
                    String.format("Minimum order value is 10.00 USD. Current total: %s", totalAmount)
            );
        }
        this.status = OrderStatus.PAID;
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
        if (status == OrderStatus.SHIPPED || status == OrderStatus.DELIVERED) {
            throw new InvalidOrderStateException(
                    String.format("Order with status %s cannot be cancelled", status)
            );
        }
        this.status = OrderStatus.CANCELLED;
    }

    /**
     * Calculates the total amount by summing all item subtotals.
     */
    private Money calculateTotalAmount() {
        Money total = new Money(BigDecimal.ZERO);
        for (OrderItem item : items) {
            total = total.add(item.calculateSubtotal());
        }
        return total;
    }

    @Override
    public String toString() {
        return String.format("Order{id=%s, status=%s, customerId=%s, totalAmount=%s, items=%d}",
                id, status, customerId, totalAmount, items.size());
    }
}
