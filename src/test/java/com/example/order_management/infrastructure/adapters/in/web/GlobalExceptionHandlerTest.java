package com.example.order_management.infrastructure.adapters.in.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void shouldHandleMethodArgumentNotValidException() {
        // Arrange
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        
        FieldError fieldError1 = new FieldError("createOrderRequest", "customerId", "Customer ID cannot be null");
        FieldError fieldError2 = new FieldError("createOrderRequest", "items", "Items cannot be empty");
        
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError1, fieldError2));

        // Act
        ResponseEntity<Map<String, Object>> response = 
                globalExceptionHandler.handleValidationExceptions(ex);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).containsKey("customerId");
        assertThat(response.getBody()).containsKey("items");
        assertThat(response.getBody().get("customerId")).isEqualTo("Customer ID cannot be null");
        assertThat(response.getBody().get("items")).isEqualTo("Items cannot be empty");
    }

    @Test
    void shouldHandleIllegalArgumentException() {
        // Arrange
        String errorMessage = "Order not found: 123e4567-e89b-12d3-a456-426614174000";
        IllegalArgumentException ex = new IllegalArgumentException(errorMessage);

        // Act
        ResponseEntity<Map<String, String>> response = 
                globalExceptionHandler.handleIllegalArgumentException(ex);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).containsKey("error");
        assertThat(response.getBody().get("error")).isEqualTo(errorMessage);
    }

    @Test
    void shouldHandleMultipleValidationErrors() {
        // Arrange
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        
        FieldError fieldError1 = new FieldError("request", "field1", "Field1 is required");
        FieldError fieldError2 = new FieldError("request", "field2", "Field2 must be positive");
        FieldError fieldError3 = new FieldError("request", "field3", "Field3 cannot be null");
        
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError1, fieldError2, fieldError3));

        // Act
        ResponseEntity<Map<String, Object>> response = 
                globalExceptionHandler.handleValidationExceptions(ex);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(3);
        assertThat(response.getBody()).containsKey("field1");
        assertThat(response.getBody()).containsKey("field2");
        assertThat(response.getBody()).containsKey("field3");
    }

    @Test
    void shouldHandleEmptyValidationErrors() {
        // Arrange
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(List.of());

        // Act
        ResponseEntity<Map<String, Object>> response = 
                globalExceptionHandler.handleValidationExceptions(ex);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEmpty();
    }

    @Test
    void shouldHandleIllegalArgumentExceptionWithNullMessage() {
        // Arrange
        IllegalArgumentException ex = new IllegalArgumentException();

        // Act
        ResponseEntity<Map<String, String>> response = 
                globalExceptionHandler.handleIllegalArgumentException(ex);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).containsKey("error");
        // Message might be null, which is acceptable
    }
}
