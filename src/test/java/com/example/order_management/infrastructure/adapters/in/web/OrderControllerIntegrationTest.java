package com.example.order_management.infrastructure.adapters.in.web;

import com.example.order_management.infrastructure.adapters.in.web.dto.CreateOrderRequest;
import com.example.order_management.infrastructure.adapters.in.web.dto.CreateOrderResponse;
import com.example.order_management.infrastructure.adapters.in.web.dto.OrderResponse;
import com.example.order_management.infrastructure.adapters.in.web.dto.PayOrderResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration test for OrderController.
 * Tests REST API endpoints end-to-end.
 */
@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("dev")
class OrderControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    private MockMvc getMockMvc() {
        if (mockMvc == null) {
            mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        }
        return mockMvc;
    }

    /**
     * Helper method to create an order for testing.
     * Reduces code duplication across test methods.
     */
    private CreateOrderResponse createOrder(UUID customerId, UUID productId, BigDecimal itemPrice, int quantity) throws Exception {
        CreateOrderRequest createRequest = new CreateOrderRequest(
                customerId,
                List.of(new CreateOrderRequest.OrderItemRequest(
                        productId,
                        quantity,
                        itemPrice
                ))
        );

        String createResponseJson = getMockMvc().perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readValue(createResponseJson, CreateOrderResponse.class);
    }

    @Test
    void shouldCreateOrder() throws Exception {
        // Given
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        CreateOrderRequest request = new CreateOrderRequest(
                customerId,
                List.of(new CreateOrderRequest.OrderItemRequest(
                        productId,
                        2,
                        new BigDecimal("15.50")
                ))
        );

        // When & Then
        String responseJson = getMockMvc().perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").exists())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.totalAmount").value(31.00))
                .andReturn()
                .getResponse()
                .getContentAsString();

        CreateOrderResponse response = objectMapper.readValue(responseJson, CreateOrderResponse.class);
        assertThat(response.orderId()).isNotNull();
        assertThat(response.status()).isEqualTo("PENDING");
    }

    @Test
    void shouldReturnBadRequestWhenCreatingOrderWithEmptyItems() throws Exception {
        // Given
        UUID customerId = UUID.randomUUID();
        CreateOrderRequest request = new CreateOrderRequest(customerId, List.of());

        // When & Then
        getMockMvc().perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGetOrderById() throws Exception {
        // Given - create an order first
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        CreateOrderResponse createResponse = createOrder(customerId, productId, new BigDecimal("20.00"), 1);
        UUID orderId = createResponse.orderId();

        // When & Then
        String responseJson = getMockMvc().perform(get("/api/v1/orders/{orderId}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(orderId.toString()))
                .andExpect(jsonPath("$.customerId").value(customerId.toString()))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items[0].productId").value(productId.toString()))
                .andReturn()
                .getResponse()
                .getContentAsString();

        OrderResponse response = objectMapper.readValue(responseJson, OrderResponse.class);
        assertThat(response.orderId()).isEqualTo(orderId);
        assertThat(response.items()).hasSize(1);
    }

    @Test
    void shouldReturnNotFoundWhenOrderDoesNotExist() throws Exception {
        // When & Then
        getMockMvc().perform(get("/api/v1/orders/{orderId}", UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldPayOrder() throws Exception {
        // Given - create an order with minimum value (>= 10.00)
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        CreateOrderResponse createResponse = createOrder(customerId, productId, new BigDecimal("20.00"), 1);
        UUID orderId = createResponse.orderId();

        // When & Then
        String responseJson = getMockMvc().perform(post("/api/v1/orders/{orderId}/pay", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(orderId.toString()))
                .andExpect(jsonPath("$.status").value("PAID"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        PayOrderResponse response = objectMapper.readValue(responseJson, PayOrderResponse.class);
        assertThat(response.orderId()).isEqualTo(orderId);
        assertThat(response.status()).isEqualTo("PAID");
    }

    @Test
    void shouldReturnConflictWhenPayingOrderBelowMinimumValue() throws Exception {
        // Given - create an order below minimum value (< 10.00)
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        CreateOrderResponse createResponse = createOrder(customerId, productId, new BigDecimal("5.00"), 1);
        UUID orderId = createResponse.orderId();

        // When & Then - should return conflict
        getMockMvc().perform(post("/api/v1/orders/{orderId}/pay", orderId))
                .andExpect(status().isConflict());
    }
}
