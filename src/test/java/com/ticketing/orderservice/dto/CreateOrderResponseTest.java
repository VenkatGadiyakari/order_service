package com.ticketing.orderservice.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CreateOrderResponseTest {

    @Test
    void testValidCreateOrderResponse() {
        UUID orderId = UUID.randomUUID();
        String status = "PENDING";
        BigDecimal totalAmount = new BigDecimal("5000.00");
        List<OrderItemResponse> items = List.of(new OrderItemResponse(UUID.randomUUID(), 2, new BigDecimal("2500.00")));
        CreateOrderResponse response = new CreateOrderResponse(orderId, status, totalAmount, items);

        assertEquals(orderId, response.getOrderId());
        assertEquals(status, response.getStatus());
        assertEquals(totalAmount, response.getTotalAmount());
        assertEquals(items, response.getItems());
    }

    @Test
    void testSettersAndGetters() {
        CreateOrderResponse response = new CreateOrderResponse();
        UUID orderId = UUID.randomUUID();
        String status = "PENDING";
        BigDecimal totalAmount = new BigDecimal("1000.00");
        List<OrderItemResponse> items = List.of(new OrderItemResponse(UUID.randomUUID(), 1, new BigDecimal("1000.00")));

        response.setOrderId(orderId);
        response.setStatus(status);
        response.setTotalAmount(totalAmount);
        response.setItems(items);

        assertEquals(orderId, response.getOrderId());
        assertEquals(status, response.getStatus());
        assertEquals(totalAmount, response.getTotalAmount());
        assertEquals(items, response.getItems());
    }

    @Test
    void testNoArgsConstructor() {
        CreateOrderResponse response = new CreateOrderResponse();
        assertNotNull(response);
        assertNull(response.getOrderId());
        assertNull(response.getStatus());
        assertNull(response.getTotalAmount());
        assertNull(response.getItems());
    }

    @Test
    void testAllArgsConstructor() {
        UUID orderId = UUID.randomUUID();
        String status = "PENDING";
        BigDecimal totalAmount = new BigDecimal("3000.00");
        List<OrderItemResponse> items = List.of(new OrderItemResponse(UUID.randomUUID(), 3, new BigDecimal("1000.00")));
        CreateOrderResponse response = new CreateOrderResponse(orderId, status, totalAmount, items);

        assertEquals(orderId, response.getOrderId());
        assertEquals(status, response.getStatus());
        assertEquals(totalAmount, response.getTotalAmount());
        assertEquals(items, response.getItems());
    }

    @Test
    void testWithNullValues() {
        CreateOrderResponse response = new CreateOrderResponse(null, null, null, null);
        assertNull(response.getOrderId());
        assertNull(response.getStatus());
        assertNull(response.getTotalAmount());
        assertNull(response.getItems());
    }
}
