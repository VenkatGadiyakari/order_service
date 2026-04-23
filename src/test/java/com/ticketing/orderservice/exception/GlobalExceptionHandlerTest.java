package com.ticketing.orderservice.exception;

import com.ticketing.orderservice.dto.ErrorResponse;
import com.ticketing.orderservice.util.AuditService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private AuditService auditService;

    @BeforeEach
    void setUp() {
        auditService = new AuditService();
        handler = new GlobalExceptionHandler(auditService);
        MDC.put("traceId", "test-trace-id");
    }

    @Test
    void testHandleUnauthorizedException() {
        UnauthorizedException exception = new UnauthorizedException("Invalid token");

        ResponseEntity<ErrorResponse> response = handler.handleUnauthorizedException(exception);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("UNAUTHORIZED", response.getBody().getErrorCode());
        assertEquals("test-trace-id", response.getBody().getTraceId());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void testHandleOrderAccessDeniedException() {
        UUID orderId = UUID.randomUUID();
        OrderAccessDeniedException exception = new OrderAccessDeniedException(orderId);

        ResponseEntity<ErrorResponse> response = handler.handleOrderAccessDeniedException(exception);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("ACCESS_DENIED", response.getBody().getErrorCode());
        assertEquals("test-trace-id", response.getBody().getTraceId());
    }

    @Test
    void testHandleOrderNotFoundException() {
        UUID orderId = UUID.randomUUID();
        OrderNotFoundException exception = new OrderNotFoundException(orderId);

        ResponseEntity<ErrorResponse> response = handler.handleNotFoundException(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("NOT_FOUND", response.getBody().getErrorCode());
        assertTrue(response.getBody().getMessage().contains(orderId.toString()));
    }

    @Test
    void testHandleTierNotFoundException() {
        UUID tierId = UUID.randomUUID();
        TierNotFoundException exception = new TierNotFoundException(tierId);

        ResponseEntity<ErrorResponse> response = handler.handleNotFoundException(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("NOT_FOUND", response.getBody().getErrorCode());
    }

    @Test
    void testHandleInsufficientInventoryException() {
        InsufficientInventoryException exception = new InsufficientInventoryException(5, 2);

        ResponseEntity<ErrorResponse> response = handler.handleConflictException(exception);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("CONFLICT", response.getBody().getErrorCode());
    }

    @Test
    void testHandleInvalidTierStatusException() {
        InvalidTierStatusException exception = new InvalidTierStatusException("CLOSED");

        ResponseEntity<ErrorResponse> response = handler.handleConflictException(exception);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("CONFLICT", response.getBody().getErrorCode());
    }

    @Test
    void testHandleInvalidEventStatusException() {
        InvalidEventStatusException exception = new InvalidEventStatusException("CANCELLED");

        ResponseEntity<ErrorResponse> response = handler.handleConflictException(exception);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("CONFLICT", response.getBody().getErrorCode());
    }

    @Test
    void testHandleQuantityExceedsMaxPerOrderException() {
        QuantityExceedsMaxPerOrderException exception = new QuantityExceedsMaxPerOrderException(10, 5);

        ResponseEntity<ErrorResponse> response = handler.handleQuantityExceedsMaxPerOrderException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("VALIDATION_FAILED", response.getBody().getErrorCode());
    }

    @Test
    void testHandlePaymentServiceException() {
        PaymentServiceException exception = new PaymentServiceException("Payment failed");

        ResponseEntity<ErrorResponse> response = handler.handlePaymentServiceException(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("PAYMENT_ERROR", response.getBody().getErrorCode());
        assertEquals("Payment service error occurred", response.getBody().getMessage());
    }

    @Test
    void testHandleGenericException() {
        Exception exception = new Exception("Unexpected error");

        ResponseEntity<ErrorResponse> response = handler.handleGenericException(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("INTERNAL_ERROR", response.getBody().getErrorCode());
        assertEquals("An unexpected error occurred", response.getBody().getMessage());
    }

    @Test
    void testHandleMethodArgumentNotValidException() {
        BindingResult bindingResult = new org.springframework.validation.BeanPropertyBindingResult(new Object(), "request");
        bindingResult.addError(new FieldError("request", "tierId", "tierId is required"));

        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<ErrorResponse> response = handler.handleValidationException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("VALIDATION_FAILED", response.getBody().getErrorCode());
        assertTrue(response.getBody().getMessage().contains("tierId is required"));
    }

    @Test
    void testErrorResponseIncludesTraceId() {
        UnauthorizedException exception = new UnauthorizedException("Test");
        ResponseEntity<ErrorResponse> response = handler.handleUnauthorizedException(exception);

        assertNotNull(response.getBody());
        assertEquals("test-trace-id", response.getBody().getTraceId());
    }

    @Test
    void testErrorResponseIncludesTimestamp() {
        OrderNotFoundException exception = new OrderNotFoundException(UUID.randomUUID());
        ResponseEntity<ErrorResponse> response = handler.handleNotFoundException(exception);

        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void testMultipleFieldValidationErrors() {
        BindingResult bindingResult = new org.springframework.validation.BeanPropertyBindingResult(new Object(), "request");
        bindingResult.addError(new FieldError("request", "tierId", "tierId is required"));
        bindingResult.addError(new FieldError("request", "quantity", "quantity must be at least 1"));

        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<ErrorResponse> response = handler.handleValidationException(exception);

        assertNotNull(response.getBody());
        String message = response.getBody().getMessage();
        assertTrue(message.contains("tierId is required"));
        assertTrue(message.contains("quantity must be at least 1"));
    }
}
