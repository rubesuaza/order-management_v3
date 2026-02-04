package com.example.management.application.services;

import com.example.management.application.ports.in.OrderManagementUseCase.OrderItemCommand;
import com.example.management.application.ports.out.OrderRepository;
import com.example.management.domain.model.Money;
import com.example.management.domain.model.Order;
import com.example.management.domain.model.OrderItem;
import com.example.management.domain.model.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderApplicationService")
class OrderApplicationServiceTest {

    @Mock
    private OrderRepository orderRepository;

    private OrderApplicationService service;

    private static final UUID CUSTOMER_ID = UUID.randomUUID();
    private static final UUID PRODUCT_ID = UUID.randomUUID();
    private static final UUID ORDER_ID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        service = new OrderApplicationService(orderRepository);
    }

    private static OrderItemCommand itemCommand(UUID productId, int qty, String amount, String currency) {
        return new OrderItemCommand(productId, qty, new BigDecimal(amount), currency);
    }

    private static OrderItem item(UUID productId, int qty, String amount, String currency) {
        return new OrderItem(productId, qty, new Money(new BigDecimal(amount), currency));
    }

    private static Order order(UUID id, UUID customerId, List<OrderItem> items, OrderStatus status) {
        Order order = new Order(id, customerId, items, LocalDateTime.now());
        if (status == OrderStatus.PAID || status == OrderStatus.SHIPPED || status == OrderStatus.DELIVERED) {
            order.markAsPaid();
            if (status == OrderStatus.SHIPPED || status == OrderStatus.DELIVERED) order.ship();
            if (status == OrderStatus.DELIVERED) order.deliver();
        } else if (status == OrderStatus.CANCELLED) {
            order.markAsPaid();
            order.cancel();
        }
        return order;
    }

    @Nested
    @DisplayName("createOrder")
    class CreateOrder {

        @Test
        void shouldCreateOrderAndDelegateToRepository() {
            List<OrderItemCommand> commands = List.of(
                    itemCommand(PRODUCT_ID, 2, "10.00", "USD")
            );
            Order savedOrder = new Order(UUID.randomUUID(), CUSTOMER_ID,
                    List.of(item(PRODUCT_ID, 2, "10.00", "USD")), LocalDateTime.now());
            when(orderRepository.save(any(Order.class))).thenAnswer(inv -> {
                Order o = inv.getArgument(0);
                return new Order(o.getId(), o.getCustomerId(), o.getItems(), o.getCreatedAt());
            });

            Order result = service.createOrder(CUSTOMER_ID, commands);

            assertThat(result).isNotNull();
            assertThat(result.getCustomerId()).isEqualTo(CUSTOMER_ID);
            assertThat(result.getItems()).hasSize(1);
            assertThat(result.getItems().get(0).getProductId()).isEqualTo(PRODUCT_ID);
            assertThat(result.getItems().get(0).getQuantity()).isEqualTo(2);
            assertThat(result.getItems().get(0).getUnitPrice().getAmount()).isEqualByComparingTo(new BigDecimal("10.00"));
            assertThat(result.getItems().get(0).getUnitPrice().getCurrency()).isEqualTo("USD");
            assertThat(result.getStatus()).isEqualTo(OrderStatus.PENDING);

            ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
            verify(orderRepository).save(captor.capture());
            assertThat(captor.getValue().getTotalAmount().getAmount()).isEqualByComparingTo(new BigDecimal("20.00"));
        }

        @Test
        void shouldUseDefaultCurrencyUsdWhenCurrencyIsNull() {
            List<OrderItemCommand> commands = List.of(
                    new OrderItemCommand(PRODUCT_ID, 1, new BigDecimal("15.00"), null)
            );
            when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

            Order result = service.createOrder(CUSTOMER_ID, commands);

            assertThat(result.getItems().get(0).getUnitPrice().getCurrency()).isEqualTo("USD");
            verify(orderRepository).save(any(Order.class));
        }
    }

    @Nested
    @DisplayName("getOrderById")
    class GetOrderById {

        @Test
        void shouldReturnOrderWhenFound() {
            Order order = order(ORDER_ID, CUSTOMER_ID, List.of(item(PRODUCT_ID, 1, "10.00", "USD")), OrderStatus.PENDING);
            when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));

            Optional<Order> result = service.getOrderById(ORDER_ID);

            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(ORDER_ID);
        }

        @Test
        void shouldReturnEmptyWhenNotFound() {
            when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.empty());

            Optional<Order> result = service.getOrderById(ORDER_ID);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("listOrders")
    class ListOrders {

        @Test
        void shouldDelegateToListOrders() {
            Order order = order(ORDER_ID, CUSTOMER_ID, List.of(item(PRODUCT_ID, 1, "10.00", "USD")), OrderStatus.PENDING);
            when(orderRepository.findAll()).thenReturn(List.of(order));

            List<Order> result = service.listOrders();

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getId()).isEqualTo(ORDER_ID);
            verify(orderRepository).findAll();
        }
    }

    @Nested
    @DisplayName("markOrderAsPaid")
    class MarkOrderAsPaid {

        @Test
        void shouldMarkOrderAsPaidAndSave() {
            Order order = order(ORDER_ID, CUSTOMER_ID, List.of(item(PRODUCT_ID, 2, "10.00", "USD")), OrderStatus.PENDING);
            when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));
            when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

            service.markOrderAsPaid(ORDER_ID);

            assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);
            verify(orderRepository).save(order);
        }

        @Test
        void shouldThrowWhenOrderNotFound() {
            when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.markOrderAsPaid(ORDER_ID))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Order not found");
        }
    }

    @Nested
    @DisplayName("shipOrder")
    class ShipOrder {

        @Test
        void shouldShipOrderAndSave() {
            Order order = order(ORDER_ID, CUSTOMER_ID, List.of(item(PRODUCT_ID, 2, "10.00", "USD")), OrderStatus.PAID);
            when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));
            when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

            service.shipOrder(ORDER_ID);

            assertThat(order.getStatus()).isEqualTo(OrderStatus.SHIPPED);
            verify(orderRepository).save(order);
        }

        @Test
        void shouldThrowWhenOrderNotFound() {
            when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.shipOrder(ORDER_ID))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Order not found");
        }
    }

    @Nested
    @DisplayName("deliverOrder")
    class DeliverOrder {

        @Test
        void shouldDeliverOrderAndSave() {
            Order order = order(ORDER_ID, CUSTOMER_ID, List.of(item(PRODUCT_ID, 2, "10.00", "USD")), OrderStatus.SHIPPED);
            when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));
            when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

            service.deliverOrder(ORDER_ID);

            assertThat(order.getStatus()).isEqualTo(OrderStatus.DELIVERED);
            verify(orderRepository).save(order);
        }

        @Test
        void shouldThrowWhenOrderNotFound() {
            when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.deliverOrder(ORDER_ID))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Order not found");
        }
    }

    @Nested
    @DisplayName("cancelOrder")
    class CancelOrder {

        @Test
        void shouldCancelOrderAndSave() {
            Order order = order(ORDER_ID, CUSTOMER_ID, List.of(item(PRODUCT_ID, 2, "10.00", "USD")), OrderStatus.PENDING);
            when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));
            when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

            service.cancelOrder(ORDER_ID);

            assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
            verify(orderRepository).save(order);
        }

        @Test
        void shouldThrowWhenOrderNotFound() {
            when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.cancelOrder(ORDER_ID))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Order not found");
        }
    }
}
