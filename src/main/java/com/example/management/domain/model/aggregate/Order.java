package com.example.management.domain.model.aggregate;

import com.example.management.domain.exception.InvalidOrderStateException;
import com.example.management.domain.model.entity.OrderItem;
import com.example.management.domain.model.valueobject.Money;
import com.example.management.domain.model.valueobject.OrderId;
import com.example.management.domain.model.valueobject.OrderStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Aggregate Root for Order.
 * Manages state transitions, item integrity, and total amount recalculation.
 * Place Order allowed only if totalAmount >= 10.00 USD.
 * Cancel allowed only if status is PENDING or PAID. Forbidden if SHIPPED.
 * Ship requires previous status to be PAID.
 */
public final class Order {

    private static final Money MINIMUM_ORDER_AMOUNT = Money.usd(java.math.BigDecimal.valueOf(10.00));

    private final OrderId id;
    private OrderStatus status;
    private final List<OrderItem> items;
    private Money totalAmount;

    public Order(OrderId id) {
        if (id == null) {
            throw new IllegalArgumentException("OrderId cannot be null");
        }
        this.id = id;
        this.status = OrderStatus.PENDING;
        this.items = new ArrayList<>();
        this.totalAmount = Money.usd(java.math.BigDecimal.ZERO);
    }

    public OrderId getId() {
        return id;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public Money getTotalAmount() {
        return totalAmount;
    }

    /**
     * Adds an item and recalculates totalAmount.
     */
    public void addItem(String productId, int quantity, Money unitPrice) {
        if (status != OrderStatus.PENDING) {
            throw new InvalidOrderStateException("Cannot add items when order status is " + status);
        }
        int nextId = items.size() + 1;
        OrderItem item = new OrderItem(nextId, productId, quantity, unitPrice);
        items.add(item);
        recalculateTotal();
    }

    /**
     * Removes an item by itemId and recalculates totalAmount.
     */
    public void removeItem(int itemId) {
        if (status != OrderStatus.PENDING) {
            throw new InvalidOrderStateException("Cannot remove items when order status is " + status);
        }
        items.removeIf(i -> i.getItemId() == itemId);
        recalculateTotal();
    }

    private void recalculateTotal() {
        Money sum = Money.usd(java.math.BigDecimal.ZERO);
        for (OrderItem item : items) {
            sum = sum.add(item.getLineTotal());
        }
        this.totalAmount = sum;
    }

    /**
     * Place Order: allowed only if totalAmount >= 10.00 USD.
     */
    public void place() {
        if (status != OrderStatus.PENDING) {
            throw new InvalidOrderStateException("Can only place order when status is PENDING, current: " + status);
        }
        if (!totalAmount.isGreaterThanOrEqual(MINIMUM_ORDER_AMOUNT)) {
            throw new InvalidOrderStateException(
                    "Minimum order amount is 10.00 USD, current total: " + totalAmount.getAmount());
        }
        // Status remains PENDING after place - order is "placed" and ready for payment
        // Per domain: "Place Order" - we interpret this as validating and confirming the order
    }

    /**
     * Pay Order: transitions from PENDING to PAID.
     */
    public void pay() {
        if (status != OrderStatus.PENDING) {
            throw new InvalidOrderStateException("Can only pay order when status is PENDING, current: " + status);
        }
        this.status = OrderStatus.PAID;
    }

    /**
     * Cancel: allowed only if status is PENDING or PAID. Forbidden if SHIPPED.
     */
    public void cancel() {
        if (status == OrderStatus.SHIPPED) {
            throw new InvalidOrderStateException("Cannot cancel order when status is SHIPPED");
        }
        if (status == OrderStatus.CANCELLED) {
            throw new InvalidOrderStateException("Order is already cancelled");
        }
        this.status = OrderStatus.CANCELLED;
    }

    /**
     * Ship: requires previous status to be PAID.
     */
    public void ship() {
        if (status != OrderStatus.PAID) {
            throw new InvalidOrderStateException("Can only ship order when status is PAID, current: " + status);
        }
        this.status = OrderStatus.SHIPPED;
    }
}
