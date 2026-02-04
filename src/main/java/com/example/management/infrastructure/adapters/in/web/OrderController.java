package com.example.management.infrastructure.adapters.in.web;

import com.example.management.application.ports.in.OrderManagementUseCase;
import com.example.management.domain.model.Order;
import com.example.management.infrastructure.adapters.in.web.dto.CreateOrderRequest;
import com.example.management.infrastructure.adapters.in.web.dto.OrderResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST input adapter for order management.
 * Delegates to OrderManagementUseCase (application layer).
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderManagementUseCase orderManagement;

    public OrderController(OrderManagementUseCase orderManagement) {
        this.orderManagement = orderManagement;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        List<OrderManagementUseCase.OrderItemCommand> items = request.items().stream()
                .map(i -> new OrderManagementUseCase.OrderItemCommand(
                        i.productId(),
                        i.quantity(),
                        i.unitPriceAmount(),
                        i.unitPriceCurrency() != null ? i.unitPriceCurrency() : "USD"))
                .toList();
        Order order = orderManagement.createOrder(request.customerId(), items);
        return ResponseEntity.status(HttpStatus.CREATED).body(OrderResponse.from(order));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable UUID id) {
        return orderManagement.getOrderById(id)
                .map(OrderResponse::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<OrderResponse> listOrders() {
        return orderManagement.listOrders().stream()
                .map(OrderResponse::from)
                .toList();
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<Void> markAsPaid(@PathVariable UUID id) {
        try {
            orderManagement.markOrderAsPaid(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.unprocessableEntity().build();
        }
    }

    @PostMapping("/{id}/ship")
    public ResponseEntity<Void> ship(@PathVariable UUID id) {
        try {
            orderManagement.shipOrder(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.unprocessableEntity().build();
        }
    }

    @PostMapping("/{id}/deliver")
    public ResponseEntity<Void> deliver(@PathVariable UUID id) {
        try {
            orderManagement.deliverOrder(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.unprocessableEntity().build();
        }
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable UUID id) {
        try {
            orderManagement.cancelOrder(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.unprocessableEntity().build();
        }
    }
}
