package com.example.management.infrastructure.adapters.out.persistence;

import com.example.management.domain.model.aggregate.Order;
import com.example.management.domain.model.entity.OrderItem;
import com.example.management.domain.model.valueobject.Money;
import com.example.management.domain.model.valueobject.OrderId;
import com.example.management.domain.model.valueobject.OrderStatus;
import com.example.management.infrastructure.persistence.entity.OrderEntity;
import com.example.management.infrastructure.persistence.entity.OrderItemEntity;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Mapper between domain Order aggregate and JPA persistence entities.
 * Implements the Repository pattern's mapping responsibility.
 */
@Component
public class OrderPersistenceMapper {

    public OrderEntity toEntity(Order order) {
        OrderEntity entity = new OrderEntity(
                order.getId().getValue(),
                toEntityStatus(order.getStatus()),
                order.getTotalAmount().getAmount(),
                order.getTotalAmount().getCurrency()
        );
        for (OrderItem item : order.getItems()) {
            OrderItemEntity itemEntity = new OrderItemEntity(
                    entity,
                    item.getItemId(),
                    item.getProductId(),
                    item.getQuantity(),
                    item.getUnitPrice().getAmount(),
                    item.getUnitPrice().getCurrency()
            );
            entity.getItems().add(itemEntity);
        }
        return entity;
    }

    public Order toDomain(OrderEntity entity) {
        List<OrderItem> items = entity.getItems().stream()
                .map(ie -> new OrderItem(
                        ie.getItemId(),
                        ie.getProductId(),
                        ie.getQuantity(),
                        Money.of(ie.getUnitPriceAmount(), ie.getUnitPriceCurrency())))
                .toList();
        Money totalAmount = Money.of(entity.getTotalAmount(), entity.getTotalCurrency());
        return Order.reconstitute(
                OrderId.of(entity.getId()),
                items,
                OrderStatus.valueOf(entity.getStatus().name()),
                totalAmount);
    }

    private OrderEntity.OrderStatusEntity toEntityStatus(OrderStatus status) {
        return OrderEntity.OrderStatusEntity.valueOf(status.name());
    }
}
