package com.example.order_management.infrastructure.adapters.in.web;

import com.example.order_management.application.ports.in.CreateOrderItemCommand;
import com.example.order_management.application.ports.in.CreateOrderUseCase;
import com.example.order_management.application.ports.in.GetOrderUseCase;
import com.example.order_management.application.ports.in.PayOrderUseCase;
import com.example.order_management.domain.exception.InvalidOrderStateException;
import com.example.order_management.domain.exception.OrderNotFoundException;
import com.example.order_management.infrastructure.adapters.in.web.dto.CreateOrderRequest;
import com.example.order_management.infrastructure.adapters.in.web.dto.CreateOrderResponse;
import com.example.order_management.infrastructure.adapters.in.web.dto.OrderResponse;
import com.example.order_management.infrastructure.adapters.in.web.dto.PayOrderResponse;
import com.example.order_management.infrastructure.adapters.in.web.mapper.OrderDtoMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.stream.Collectors;

/**
 * REST controller for order management operations.
 * Implements the REST API endpoints as specified.
 */
@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final CreateOrderUseCase createOrderUseCase;
    private final GetOrderUseCase getOrderUseCase;
    private final PayOrderUseCase payOrderUseCase;
    private final OrderDtoMapper dtoMapper;

    public OrderController(
            CreateOrderUseCase createOrderUseCase,
            GetOrderUseCase getOrderUseCase,
            PayOrderUseCase payOrderUseCase,
            OrderDtoMapper dtoMapper) {
        this.createOrderUseCase = createOrderUseCase;
        this.getOrderUseCase = getOrderUseCase;
        this.payOrderUseCase = payOrderUseCase;
        this.dtoMapper = dtoMapper;
    }

    /**
     * Creates a new order.
     * POST /api/v1/orders
     */
    @PostMapping
    public ResponseEntity<CreateOrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        var orderItemCommands = request.items().stream()
                .map(item -> new CreateOrderItemCommand(
                        item.productId(),
                        item.quantity(),
                        item.unitPrice()
                ))
                .collect(Collectors.toList());

        var order = createOrderUseCase.createOrder(request.customerId(), orderItemCommands);
        var response = dtoMapper.toCreateResponse(order);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Retrieves an order by ID.
     * GET /api/v1/orders/{orderId}
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable UUID orderId) {
        return getOrderUseCase.getOrderById(orderId)
                .map(order -> ResponseEntity.ok(dtoMapper.toOrderResponse(order)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Processes payment for an order.
     * POST /api/v1/orders/{orderId}/pay
     */
    @PostMapping("/{orderId}/pay")
    public ResponseEntity<PayOrderResponse> payOrder(@PathVariable UUID orderId) {
        try {
            var order = payOrderUseCase.payOrder(orderId);
            var response = dtoMapper.toPayResponse(order);
            return ResponseEntity.ok(response);
        } catch (InvalidOrderStateException e) {
            // Order cannot be paid (e.g., already paid, cancelled, or minimum value not met)
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (OrderNotFoundException e) {
            // Order not found
            return ResponseEntity.notFound().build();
        }
    }
}
