package com.ticketing.orderservice.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CreateOrderRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidCreateOrderRequest() {
        UUID eventId = UUID.randomUUID();
        List<OrderItemRequest> items = List.of(new OrderItemRequest(UUID.randomUUID(), 2));
        CreateOrderRequest request = new CreateOrderRequest(eventId, items);

        Set<ConstraintViolation<CreateOrderRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
        assertEquals(eventId, request.getEventId());
        assertEquals(items, request.getItems());
    }

    @Test
    void testCreateOrderRequestWithNullEventId() {
        List<OrderItemRequest> items = List.of(new OrderItemRequest(UUID.randomUUID(), 1));
        CreateOrderRequest request = new CreateOrderRequest(null, items);

        Set<ConstraintViolation<CreateOrderRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("eventId")));
    }

    @Test
    void testCreateOrderRequestWithNullItems() {
        CreateOrderRequest request = new CreateOrderRequest(UUID.randomUUID(), null);

        Set<ConstraintViolation<CreateOrderRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testCreateOrderRequestWithEmptyItems() {
        CreateOrderRequest request = new CreateOrderRequest(UUID.randomUUID(), Collections.emptyList());

        Set<ConstraintViolation<CreateOrderRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testSettersAndGetters() {
        CreateOrderRequest request = new CreateOrderRequest();
        UUID eventId = UUID.randomUUID();
        List<OrderItemRequest> items = List.of(new OrderItemRequest(UUID.randomUUID(), 3));

        request.setEventId(eventId);
        request.setItems(items);

        assertEquals(eventId, request.getEventId());
        assertEquals(items, request.getItems());
    }

    @Test
    void testNoArgsConstructor() {
        CreateOrderRequest request = new CreateOrderRequest();
        assertNotNull(request);
        assertNull(request.getEventId());
        assertNull(request.getItems());
    }

    @Test
    void testAllArgsConstructor() {
        UUID eventId = UUID.randomUUID();
        List<OrderItemRequest> items = List.of(new OrderItemRequest(UUID.randomUUID(), 1));
        CreateOrderRequest request = new CreateOrderRequest(eventId, items);

        assertEquals(eventId, request.getEventId());
        assertEquals(items, request.getItems());
    }
}
