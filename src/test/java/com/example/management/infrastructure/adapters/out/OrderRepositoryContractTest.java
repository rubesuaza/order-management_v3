package com.example.management.infrastructure.adapters.out;

import com.example.management.application.ports.out.OrderRepository;
import com.example.management.domain.model.Money;
import com.example.management.domain.model.Order;
import com.example.management.domain.model.OrderItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Contract tests for any OrderRepository implementation.
 * Run against InMemoryOrderRepository (or other implementations) to ensure consistent behavior.
 */
@DisplayName("OrderRepository contract")
abstract class OrderRepositoryContractTest {

    abstract OrderRepository createRepository();

    private OrderRepository repository;

    @BeforeEach
    void setUp() {
        repository = createRepository();
    }

    private static Order newOrder(UUID id, UUID customerId, List<OrderItem> items) {
        return new Order(id, customerId, items, LocalDateTime.now());
    }

    private static OrderItem newItem(UUID productId, int qty, String amount, String currency) {
        return new OrderItem(productId, qty, new Money(new BigDecimal(amount), currency));
    }

    @Nested
    @DisplayName("save and findById")
    class SaveAndFindById {

        @Test
        void save_persists_order_and_findById_returns_it() {
            UUID id = UUID.randomUUID();
            UUID customerId = UUID.randomUUID();
            List<OrderItem> items = List.of(
                    newItem(UUID.randomUUID(), 2, "5.00", "USD")
            );
            Order order = newOrder(id, customerId, items);

            Order saved = repository.save(order);
            assertThat(saved).isNotNull();
            assertThat(saved.getId()).isEqualTo(id);

            Optional<Order> found = repository.findById(id);
            assertThat(found).isPresent();
            assertThat(found.get().getId()).isEqualTo(id);
            assertThat(found.get().getCustomerId()).isEqualTo(customerId);
            assertThat(found.get().getItems()).hasSize(1);
        }

        @Test
        void findById_returns_empty_when_not_found() {
            Optional<Order> found = repository.findById(UUID.randomUUID());
            assertThat(found).isEmpty();
        }

        @Test
        void save_overwrites_existing_order_with_same_id() {
            UUID id = UUID.randomUUID();
            Order first = newOrder(id, UUID.randomUUID(),
                    List.of(newItem(UUID.randomUUID(), 1, "10.00", "USD")));
            repository.save(first);

            Order second = newOrder(id, UUID.randomUUID(),
                    List.of(newItem(UUID.randomUUID(), 3, "2.00", "USD")));
            repository.save(second);

            Optional<Order> found = repository.findById(id);
            assertThat(found).isPresent();
            assertThat(found.get().getItems()).hasSize(1);
            assertThat(found.get().getItems().get(0).getQuantity()).isEqualTo(3);
        }
    }

    @Nested
    @DisplayName("findAll")
    class FindAll {

        @Test
        void findAll_returns_empty_when_no_orders() {
            assertThat(repository.findAll()).isEmpty();
        }

        @Test
        void findAll_returns_all_saved_orders() {
            Order a = newOrder(UUID.randomUUID(), UUID.randomUUID(),
                    List.of(newItem(UUID.randomUUID(), 1, "1.00", "USD")));
            Order b = newOrder(UUID.randomUUID(), UUID.randomUUID(),
                    List.of(newItem(UUID.randomUUID(), 1, "2.00", "USD")));
            repository.save(a);
            repository.save(b);

            List<Order> all = repository.findAll();
            assertThat(all).hasSize(2);
            assertThat(all).extracting(Order::getId).containsExactlyInAnyOrder(a.getId(), b.getId());
        }
    }
}
