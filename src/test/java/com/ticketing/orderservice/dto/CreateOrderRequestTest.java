package com.ticketing.orderservice.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        UUID tierId = UUID.randomUUID();
        CreateOrderRequest request = new CreateOrderRequest(tierId, 2);

        Set<ConstraintViolation<CreateOrderRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
        assertEquals(tierId, request.getTierId());
        assertEquals(2, request.getQuantity());
    }

    @Test
    void testCreateOrderRequestWithNullTierId() {
        CreateOrderRequest request = new CreateOrderRequest(null, 2);

        Set<ConstraintViolation<CreateOrderRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertTrue(violations.iterator().next().getMessage().contains("tierId"));
    }

    @Test
    void testCreateOrderRequestWithNullQuantity() {
        CreateOrderRequest request = new CreateOrderRequest(UUID.randomUUID(), null);

        Set<ConstraintViolation<CreateOrderRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testCreateOrderRequestWithZeroQuantity() {
        CreateOrderRequest request = new CreateOrderRequest(UUID.randomUUID(), 0);

        Set<ConstraintViolation<CreateOrderRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testCreateOrderRequestWithNegativeQuantity() {
        CreateOrderRequest request = new CreateOrderRequest(UUID.randomUUID(), -1);

        Set<ConstraintViolation<CreateOrderRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testSettersAndGetters() {
        CreateOrderRequest request = new CreateOrderRequest();
        UUID tierId = UUID.randomUUID();
        Integer quantity = 5;

        request.setTierId(tierId);
        request.setQuantity(quantity);

        assertEquals(tierId, request.getTierId());
        assertEquals(quantity, request.getQuantity());
    }

    @Test
    void testNoArgsConstructor() {
        CreateOrderRequest request = new CreateOrderRequest();
        assertNotNull(request);
        assertNull(request.getTierId());
        assertNull(request.getQuantity());
    }

    @Test
    void testAllArgsConstructor() {
        UUID tierId = UUID.randomUUID();
        Integer quantity = 3;
        CreateOrderRequest request = new CreateOrderRequest(tierId, quantity);

        assertEquals(tierId, request.getTierId());
        assertEquals(quantity, request.getQuantity());
    }
}
