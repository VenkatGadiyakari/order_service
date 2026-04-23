package com.ticketing.orderservice.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    @Test
    void testOrderCreation() {
        UUID id = UUID.randomUUID();
        UUID buyerId = UUID.randomUUID();
        OrderStatus status = OrderStatus.PENDING;
        BigDecimal totalAmount = new BigDecimal("5000.00");
        String razorpayPaymentLinkId = "plink_test_123";
        Instant now = Instant.now();

        Order order = new Order(id, buyerId, status, totalAmount, razorpayPaymentLinkId, now, now);

        assertEquals(id, order.getId());
        assertEquals(buyerId, order.getBuyerId());
        assertEquals(status, order.getStatus());
        assertEquals(totalAmount, order.getTotalAmount());
        assertEquals(razorpayPaymentLinkId, order.getRazorpayPaymentLinkId());
        assertEquals(now, order.getCreatedAt());
        assertEquals(now, order.getUpdatedAt());
        assertNotNull(order.getItems());
        assertTrue(order.getItems().isEmpty());
    }

    @Test
    void testOrderSettersAndGetters() {
        Order order = new Order();
        UUID id = UUID.randomUUID();
        UUID buyerId = UUID.randomUUID();
        OrderStatus status = OrderStatus.CONFIRMED;
        BigDecimal totalAmount = new BigDecimal("3000.00");
        String razorpayPaymentLinkId = "plink_test_456";
        Instant createdAt = Instant.now();
        Instant updatedAt = Instant.now();

        order.setId(id);
        order.setBuyerId(buyerId);
        order.setStatus(status);
        order.setTotalAmount(totalAmount);
        order.setRazorpayPaymentLinkId(razorpayPaymentLinkId);
        order.setCreatedAt(createdAt);
        order.setUpdatedAt(updatedAt);

        assertEquals(id, order.getId());
        assertEquals(buyerId, order.getBuyerId());
        assertEquals(status, order.getStatus());
        assertEquals(totalAmount, order.getTotalAmount());
        assertEquals(razorpayPaymentLinkId, order.getRazorpayPaymentLinkId());
        assertEquals(createdAt, order.getCreatedAt());
        assertEquals(updatedAt, order.getUpdatedAt());
    }

    @Test
    void testAddOrderItem() {
        Order order = new Order();
        OrderItem item = new OrderItem();
        item.setId(UUID.randomUUID());

        order.addItem(item);

        assertEquals(1, order.getItems().size());
        assertEquals(item, order.getItems().get(0));
        assertEquals(order, item.getOrder());
    }

    @Test
    void testRemoveOrderItem() {
        Order order = new Order();
        OrderItem item = new OrderItem();
        item.setId(UUID.randomUUID());

        order.addItem(item);
        assertEquals(1, order.getItems().size());

        order.removeItem(item);
        assertEquals(0, order.getItems().size());
        assertNull(item.getOrder());
    }

    @Test
    void testAddMultipleItems() {
        Order order = new Order();
        OrderItem item1 = new OrderItem();
        OrderItem item2 = new OrderItem();
        item1.setId(UUID.randomUUID());
        item2.setId(UUID.randomUUID());

        order.addItem(item1);
        order.addItem(item2);

        assertEquals(2, order.getItems().size());
        assertTrue(order.getItems().contains(item1));
        assertTrue(order.getItems().contains(item2));
    }

    @Test
    void testNoArgsConstructor() {
        Order order = new Order();
        assertNotNull(order);
        assertNull(order.getId());
        assertNull(order.getBuyerId());
        assertNull(order.getStatus());
        assertNull(order.getTotalAmount());
        assertNull(order.getRazorpayPaymentLinkId());
        assertNull(order.getCreatedAt());
        assertNull(order.getUpdatedAt());
        assertNotNull(order.getItems());
        assertTrue(order.getItems().isEmpty());
    }

    @Test
    void testOrderStatusTransition() {
        Order order = new Order();
        order.setStatus(OrderStatus.PENDING);
        assertEquals(OrderStatus.PENDING, order.getStatus());

        order.setStatus(OrderStatus.CONFIRMED);
        assertEquals(OrderStatus.CONFIRMED, order.getStatus());

        order.setStatus(OrderStatus.FAILED);
        assertEquals(OrderStatus.FAILED, order.getStatus());
    }
}
