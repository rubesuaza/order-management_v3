package com.example.management.application.service;

import com.example.management.application.port.in.CreateOrderCommand;
import com.example.management.application.port.in.CreateOrderUseCase;
import com.example.management.application.port.in.OrderItemRequest;
import com.example.management.domain.port.out.OrderPersistencePort;
import com.example.management.domain.OrderStatus;
import com.example.management.domain.model.Order;
import com.example.management.domain.model.OrderId;
import com.example.management.domain.model.OrderItem;
import com.example.management.domain.valueobject.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
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
    private OrderPersistencePort orderPersistence;

    private OrderApplicationService service;

    private static final UUID CUSTOMER_ID = UUID.randomUUID();
    private static final UUID PRODUCT_ID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        service = new OrderApplicationService(orderPersistence);
    }

    @Nested
    @DisplayName("create")
    class Create {
        @Test
        void createsOrderWithItemsAndSaves() {
            OrderItemRequest itemReq = new OrderItemRequest(PRODUCT_ID, 2, new BigDecimal("5.00"));
            CreateOrderCommand command = new CreateOrderCommand(CUSTOMER_ID, List.of(itemReq));
            Order savedOrder = new Order(UUID.randomUUID(), CUSTOMER_ID,
                    List.of(new OrderItem(PRODUCT_ID, 2, new Money(new BigDecimal("5.00")))));
            when(orderPersistence.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

            Order result = service.create(command);

            assertThat(result.getCustomerId()).isEqualTo(CUSTOMER_ID);
            assertThat(result.getStatus()).isEqualTo(OrderStatus.PENDING);
            assertThat(result.getItems()).hasSize(1);
            assertThat(result.getTotalAmount().getAmount()).isEqualByComparingTo("10.00");
            verify(orderPersistence).save(any(Order.class));
        }

        @Test
        void rejectsEmptyItems() {
            assertThatThrownBy(() -> service.create(new CreateOrderCommand(CUSTOMER_ID, List.of())))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("at least one");
        }

        @Test
        void rejectsNullItems() {
            assertThatThrownBy(() -> service.create(new CreateOrderCommand(CUSTOMER_ID, null)))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("at least one");
        }
    }

    @Nested
    @DisplayName("getById")
    class GetById {
        @Test
        void returnsOrderWhenFound() {
            OrderId orderId = new OrderId(UUID.randomUUID());
            Order order = new Order(orderId.getValue(), CUSTOMER_ID,
                    List.of(new OrderItem(PRODUCT_ID, 1, new Money(new BigDecimal("15.00")))));
            when(orderPersistence.findById(orderId)).thenReturn(Optional.of(order));

            assertThat(service.getById(orderId)).contains(order);
        }

        @Test
        void returnsEmptyWhenNotFound() {
            OrderId orderId = new OrderId(UUID.randomUUID());
            when(orderPersistence.findById(orderId)).thenReturn(Optional.empty());

            assertThat(service.getById(orderId)).isEmpty();
        }
    }

    @Nested
    @DisplayName("pay")
    class Pay {
        @Test
        void marksOrderAsPaidAndSaves() {
            OrderId orderId = new OrderId(UUID.randomUUID());
            Order order = new Order(orderId.getValue(), CUSTOMER_ID,
                    List.of(new OrderItem(PRODUCT_ID, 1, new Money(new BigDecimal("15.00")))));
            when(orderPersistence.findById(orderId)).thenReturn(Optional.of(order));
            when(orderPersistence.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

            Order result = service.pay(orderId);

            assertThat(result.getStatus()).isEqualTo(OrderStatus.PAID);
            verify(orderPersistence).save(order);
        }

        @Test
        void throwsWhenOrderNotFound() {
            OrderId orderId = new OrderId(UUID.randomUUID());
            when(orderPersistence.findById(orderId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.pay(orderId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Order not found");
        }
    }
}
