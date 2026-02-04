package com.example.management.infrastructure.adapters.in.web;

import com.example.management.application.ports.out.OrderRepository;
import com.example.management.domain.model.Money;
import com.example.management.domain.model.Order;
import com.example.management.domain.model.OrderItem;
import com.example.management.infrastructure.adapters.out.persistence.InMemoryOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("OrderController integration")
class OrderControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    OrderRepository orderRepository;

    @BeforeEach
    void setUp() {
        if (orderRepository instanceof InMemoryOrderRepository inMem) {
            inMem.clear();
        }
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        OrderRepository inMemoryOrderRepository() {
            return new InMemoryOrderRepository();
        }
    }

    @Nested
    @DisplayName("POST /api/orders")
    class CreateOrder {

        @Test
        void creates_order_and_returns_201() throws Exception {
            String body = """
                    {
                      "customerId": "550e8400-e29b-41d4-a716-446655440000",
                      "items": [
                        { "productId": "660e8400-e29b-41d4-a716-446655440001", "quantity": 2, "unitPriceAmount": 5.50, "unitPriceCurrency": "USD" }
                      ]
                    }
                    """;
            mockMvc.perform(post("/api/orders")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").exists())
                    .andExpect(jsonPath("$.customerId").value("550e8400-e29b-41d4-a716-446655440000"))
                    .andExpect(jsonPath("$.status").value("PENDING"))
                    .andExpect(jsonPath("$.items", hasSize(1)))
                    .andExpect(jsonPath("$.totalAmount").value(11.0));
        }

        @Test
        void rejects_empty_items_with_400() throws Exception {
            String body = """
                    {
                      "customerId": "550e8400-e29b-41d4-a716-446655440000",
                      "items": []
                    }
                    """;
            mockMvc.perform(post("/api/orders")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("GET /api/orders/{id}")
    class GetOrder {

        @Test
        void returns_200_and_order_when_found() throws Exception {
            Order order = persistOrder();
            mockMvc.perform(get("/api/orders/" + order.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(order.getId().toString()))
                    .andExpect(jsonPath("$.status").value("PENDING"));
        }

        @Test
        void returns_404_when_not_found() throws Exception {
            mockMvc.perform(get("/api/orders/" + UUID.randomUUID()))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("POST /api/orders/{id}/pay")
    class MarkAsPaid {

        @Test
        void returns_204_when_order_exists_and_total_ge_10() throws Exception {
            Order order = persistOrderWithTotal("12.00");
            mockMvc.perform(post("/api/orders/" + order.getId() + "/pay"))
                    .andExpect(status().isNoContent());
        }

        @Test
        void returns_404_when_order_not_found() throws Exception {
            mockMvc.perform(post("/api/orders/" + UUID.randomUUID() + "/pay"))
                    .andExpect(status().isNotFound());
        }
    }

    private Order persistOrder() {
        Order order = new Order(
                UUID.randomUUID(),
                UUID.randomUUID(),
                List.of(new OrderItem(UUID.randomUUID(), 1, new Money(new BigDecimal("10.00"), "USD"))),
                LocalDateTime.now());
        return orderRepository.save(order);
    }

    private Order persistOrderWithTotal(String total) {
        Order order = new Order(
                UUID.randomUUID(),
                UUID.randomUUID(),
                List.of(new OrderItem(UUID.randomUUID(), 1, new Money(new BigDecimal(total), "USD"))),
                LocalDateTime.now());
        return orderRepository.save(order);
    }
}
