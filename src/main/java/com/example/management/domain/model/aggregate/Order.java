package com.example.management.domain.model.aggregate;

import com.example.management.domain.exception.InvalidItemException;
import com.example.management.domain.exception.InvalidOrderStateException;
import com.example.management.domain.model.entity.OrderItem;
import com.example.management.domain.model.valueobject.Money;
import com.example.management.domain.model.valueobject.OrderStatus;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Aggregate Root for Order. Manages state transitions, item integrity, and total amount recalculation.
 */
public class Order {

    private static final Money MINIMUM_ORDER_AMOUNT = Money.usd(new BigDecimal("10.00"));

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
        this.totalAmount = Money.usd(BigDecimal.ZERO);
    }

    /**
     * Reconstitution from persistence. Used by infrastructure adapters to restore Order state.
     */
    public Order(OrderId id, OrderStatus status, List<OrderItem> items, Money totalAmount) {
        if (id == null) {
            throw new IllegalArgumentException("OrderId cannot be null");
        }
        this.id = id;
        this.status = status != null ? status : OrderStatus.PENDING;
        this.items = items != null ? new ArrayList<>(items) : new ArrayList<>();
        this.totalAmount = totalAmount != null ? totalAmount : Money.usd(BigDecimal.ZERO);
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
    public void addItem(OrderItem item) {
        if (item == null) {
            throw new InvalidItemException("Order item cannot be null");
        }
        if (status != OrderStatus.PENDING) {
            throw new InvalidOrderStateException("Cannot add items to order in status: " + status);
        }
        ensureCurrencyConsistency(item.getUnitPrice());
        items.add(item);
        recalculateTotal();
    }

    /**
     * Removes an item by id and recalculates totalAmount.
     */
    public void removeItem(UUID itemId) {
        if (status != OrderStatus.PENDING) {
            throw new InvalidOrderStateException("Cannot remove items from order in status: " + status);
        }
        boolean removed = items.removeIf(i -> i.getId().equals(itemId));
        if (removed) {
            recalculateTotal();
        }
    }

    private void recalculateTotal() {
        if (items.isEmpty()) {
            totalAmount = Money.usd(BigDecimal.ZERO);
            return;
        }
        Money sum = items.get(0).getLineTotal();
        for (int i = 1; i < items.size(); i++) {
            sum = sum.add(items.get(i).getLineTotal());
        }
        totalAmount = sum;
    }

    private void ensureCurrencyConsistency(Money money) {
        if (!items.isEmpty() && !items.get(0).getUnitPrice().getCurrency().equals(money.getCurrency())) {
            throw new com.example.management.domain.exception.CurrencyMismatchException(
                    "All items must use the same currency as the order");
        }
    }

    /**
     * Place Order: Allowed only if totalAmount >= 10.00 USD.
     */
    public void place() {
        if (status != OrderStatus.PENDING) {
            throw new InvalidOrderStateException("Order can only be placed when status is PENDING");
        }
        if (!totalAmount.isGreaterThanOrEqual(MINIMUM_ORDER_AMOUNT)) {
            throw new InvalidOrderStateException(
                    "Minimum order amount is $10.00 USD. Current total: " + totalAmount.getAmount());
        }
        // Place does not change status - it validates. Status remains PENDING until paid.
        // Per use case UC-ORD-01 Create Order, the order is created in PENDING.
    }

    /**
     * Pay Order: Transitions from PENDING to PAID.
     */
    public void pay() {
        if (status != OrderStatus.PENDING) {
            throw new InvalidOrderStateException("Order can only be paid when status is PENDING. Current: " + status);
        }
        this.status = OrderStatus.PAID;
    }

    /**
     * Cancel: Allowed only if status is PENDING or PAID. Forbidden if SHIPPED.
     */
    public void cancel() {
        if (status == OrderStatus.SHIPPED) {
            throw new InvalidOrderStateException("Cannot cancel order that has been shipped");
        }
        if (status == OrderStatus.CANCELLED) {
            throw new InvalidOrderStateException("Order is already cancelled");
        }
        this.status = OrderStatus.CANCELLED;
    }

    /**
     * Ship: Requires the previous status to be PAID.
     */
    public void ship() {
        if (status != OrderStatus.PAID) {
            throw new InvalidOrderStateException("Order can only be shipped when status is PAID. Current: " + status);
        }
        this.status = OrderStatus.SHIPPED;
    }
}
