package com.example.management.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * JPA entity for Order persistence.
 * Maps to domain aggregate Order. Used only within infrastructure layer.
 */
@Entity
@Table(name = "orders")
public class OrderEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private OrderStatusEntity status;

    @Column(name = "total_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "total_currency", nullable = false, length = 3)
    private String totalCurrency;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("itemId")
    private List<OrderItemEntity> items = new ArrayList<>();

    protected OrderEntity() {
    }

    public OrderEntity(UUID id, OrderStatusEntity status, BigDecimal totalAmount, String totalCurrency) {
        this.id = id;
        this.status = status;
        this.totalAmount = totalAmount;
        this.totalCurrency = totalCurrency;
    }

    public UUID getId() {
        return id;
    }

    public OrderStatusEntity getStatus() {
        return status;
    }

    public void setStatus(OrderStatusEntity status) {
        this.status = status;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getTotalCurrency() {
        return totalCurrency;
    }

    public List<OrderItemEntity> getItems() {
        return items;
    }

    public void setItems(List<OrderItemEntity> items) {
        this.items.clear();
        if (items != null) {
            this.items.addAll(items);
        }
    }

    public enum OrderStatusEntity {
        PENDING,
        PAID,
        SHIPPED,
        CANCELLED
    }
}
