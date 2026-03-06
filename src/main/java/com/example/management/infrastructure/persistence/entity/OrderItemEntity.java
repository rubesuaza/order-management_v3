package com.example.management.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

/**
 * JPA entity for OrderItem persistence.
 * Maps to domain entity OrderItem. Used only within infrastructure layer.
 */
@Entity
@Table(name = "order_items", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"order_id", "item_id"})
})
public class OrderItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pk;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order;

    @Column(name = "item_id", nullable = false)
    private int itemId;

    @Column(name = "product_id", nullable = false, length = 255)
    private String productId;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "unit_price_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal unitPriceAmount;

    @Column(name = "unit_price_currency", nullable = false, length = 3)
    private String unitPriceCurrency;

    protected OrderItemEntity() {
    }

    public OrderItemEntity(OrderEntity order, int itemId, String productId, int quantity,
                           BigDecimal unitPriceAmount, String unitPriceCurrency) {
        this.order = order;
        this.itemId = itemId;
        this.productId = productId;
        this.quantity = quantity;
        this.unitPriceAmount = unitPriceAmount;
        this.unitPriceCurrency = unitPriceCurrency;
    }

    public Long getPk() {
        return pk;
    }

    public OrderEntity getOrder() {
        return order;
    }

    public void setOrder(OrderEntity order) {
        this.order = order;
    }

    public int getItemId() {
        return itemId;
    }

    public String getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getUnitPriceAmount() {
        return unitPriceAmount;
    }

    public String getUnitPriceCurrency() {
        return unitPriceCurrency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderItemEntity that = (OrderItemEntity) o;
        return pk != null && Objects.equals(pk, that.pk);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
