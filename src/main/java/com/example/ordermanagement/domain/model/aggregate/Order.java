package com.example.ordermanagement.domain.model.aggregate;

import com.example.ordermanagement.domain.exception.CurrencyMismatchException;
import com.example.ordermanagement.domain.exception.InvalidOrderStateException;
import com.example.ordermanagement.domain.model.entity.OrderItem;
import com.example.ordermanagement.domain.model.valueobject.Money;
import com.example.ordermanagement.domain.model.valueobject.OrderId;
import com.example.ordermanagement.domain.model.valueobject.OrderStatus;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Aggregate Root for Order.
 * Manages state transitions, item integrity, and total amount recalculation.
 */
public final class Order {

    private static final BigDecimal MIN_PLACE_AMOUNT = new BigDecimal("10.00");
    private static final String DEFAULT_CURRENCY = "USD";

    private final OrderId id;
    private OrderStatus status;
    private final List<OrderItem> items;
    private Money totalAmount;

    private Order(OrderId id, OrderStatus status, List<OrderItem> items, Money totalAmount) {
        this.id = id;
        this.status = status;
        this.items = new ArrayList<>(items);
        this.totalAmount = totalAmount;
    }

    public static Order create(OrderId id) {
        return new Order(id, OrderStatus.PENDING, List.of(), new Money(BigDecimal.ZERO, DEFAULT_CURRENCY));
    }

    /**
     * Recreates an Order from persisted state.
     * Used when loading from persistence layer.
     */
    public static Order restore(OrderId id, OrderStatus status, List<OrderItem> items, Money totalAmount) {
        return new Order(id, status, new ArrayList<>(items), totalAmount);
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

    public void addItem(OrderItem item) {
        if (item.getUnitPrice().getCurrency().equals(totalAmount.getCurrency())) {
            items.add(item);
            recalculateTotal();
        } else {
            throw new CurrencyMismatchException(
                    "Item currency " + item.getUnitPrice().getCurrency() + " does not match order currency " + totalAmount.getCurrency());
        }
    }

    public void removeItem(UUID itemId) {
        items.removeIf(i -> i.getId().equals(itemId));
        recalculateTotal();
    }

    private void recalculateTotal() {
        if (items.isEmpty()) {
            totalAmount = new Money(BigDecimal.ZERO, totalAmount.getCurrency());
        } else {
            Money sum = items.get(0).getLineTotal();
            for (int i = 1; i < items.size(); i++) {
                sum = sum.add(items.get(i).getLineTotal());
            }
            totalAmount = sum;
        }
    }

    /**
     * Place order: allowed only if totalAmount >= 10.00 USD.
     */
    public void place() {
        if (totalAmount.getAmount().compareTo(MIN_PLACE_AMOUNT) < 0) {
            throw new InvalidOrderStateException(
                    "Order total must be at least 10.00 USD to place. Current: " + totalAmount.getAmount() + " " + totalAmount.getCurrency());
        }
        if (status != OrderStatus.PENDING) {
            throw new InvalidOrderStateException("Order can only be placed when status is PENDING");
        }
        // Status remains PENDING - ready for payment
    }

    public void pay() {
        if (status != OrderStatus.PENDING) {
            throw new InvalidOrderStateException("Order can only be paid when status is PENDING");
        }
        status = OrderStatus.PAID;
    }

    /**
     * Cancel: allowed only if status is PENDING or PAID. Forbidden if SHIPPED.
     */
    public void cancel() {
        if (status == OrderStatus.SHIPPED) {
            throw new InvalidOrderStateException("Cannot cancel order that has been shipped");
        }
        if (status == OrderStatus.CANCELLED) {
            throw new InvalidOrderStateException("Order is already cancelled");
        }
        status = OrderStatus.CANCELLED;
    }

    /**
     * Ship: requires previous status to be PAID.
     */
    public void ship() {
        if (status != OrderStatus.PAID) {
            throw new InvalidOrderStateException("Order can only be shipped when status is PAID. Current: " + status);
        }
        status = OrderStatus.SHIPPED;
    }
}
