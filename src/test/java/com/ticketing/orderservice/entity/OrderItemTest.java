package com.ticketing.orderservice.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OrderItemTest {

    @Test
    void testOrderItemCreation() {
        UUID id = UUID.randomUUID();
        Order order = new Order();
        UUID tierId = UUID.randomUUID();
        String tierName = "VIP";
        String eventTitle = "Rock Concert 2026";
        Instant eventDate = Instant.now();
        Integer quantity = 2;
        BigDecimal unitPrice = new BigDecimal("2500.00");
        Instant createdAt = Instant.now();

        OrderItem item = new OrderItem(id, order, tierId, tierName, eventTitle, eventDate, quantity, unitPrice, createdAt);

        assertEquals(id, item.getId());
        assertEquals(order, item.getOrder());
        assertEquals(tierId, item.getTierId());
        assertEquals(tierName, item.getTierName());
        assertEquals(eventTitle, item.getEventTitle());
        assertEquals(eventDate, item.getEventDate());
        assertEquals(quantity, item.getQuantity());
        assertEquals(unitPrice, item.getUnitPrice());
        assertEquals(createdAt, item.getCreatedAt());
    }

    @Test
    void testOrderItemSettersAndGetters() {
        OrderItem item = new OrderItem();
        UUID id = UUID.randomUUID();
        Order order = new Order();
        UUID tierId = UUID.randomUUID();
        String tierName = "Standard";
        String eventTitle = "Jazz Night";
        Instant eventDate = Instant.now();
        Integer quantity = 5;
        BigDecimal unitPrice = new BigDecimal("1000.00");
        Instant createdAt = Instant.now();

        item.setId(id);
        item.setOrder(order);
        item.setTierId(tierId);
        item.setTierName(tierName);
        item.setEventTitle(eventTitle);
        item.setEventDate(eventDate);
        item.setQuantity(quantity);
        item.setUnitPrice(unitPrice);
        item.setCreatedAt(createdAt);

        assertEquals(id, item.getId());
        assertEquals(order, item.getOrder());
        assertEquals(tierId, item.getTierId());
        assertEquals(tierName, item.getTierName());
        assertEquals(eventTitle, item.getEventTitle());
        assertEquals(eventDate, item.getEventDate());
        assertEquals(quantity, item.getQuantity());
        assertEquals(unitPrice, item.getUnitPrice());
        assertEquals(createdAt, item.getCreatedAt());
    }

    @Test
    void testNoArgsConstructor() {
        OrderItem item = new OrderItem();
        assertNotNull(item);
        assertNull(item.getId());
        assertNull(item.getOrder());
        assertNull(item.getTierId());
        assertNull(item.getTierName());
        assertNull(item.getEventTitle());
        assertNull(item.getEventDate());
        assertNull(item.getQuantity());
        assertNull(item.getUnitPrice());
        assertNull(item.getCreatedAt());
    }

    @Test
    void testOrderItemWithNullOrder() {
        OrderItem item = new OrderItem();
        item.setOrder(null);
        assertNull(item.getOrder());
    }

    @Test
    void testOrderItemQuantityBoundaries() {
        OrderItem item = new OrderItem();
        item.setQuantity(1);
        assertEquals(1, item.getQuantity());

        item.setQuantity(100);
        assertEquals(100, item.getQuantity());
    }

    @Test
    void testOrderItemPricePrecision() {
        OrderItem item = new OrderItem();
        BigDecimal price = new BigDecimal("1234.56");
        item.setUnitPrice(price);
        assertEquals(price, item.getUnitPrice());
        assertEquals(2, item.getUnitPrice().scale());
    }

    @Test
    void testEventDateStorage() {
        OrderItem item = new OrderItem();
        Instant eventDate = Instant.parse("2026-05-20T19:00:00Z");
        item.setEventDate(eventDate);
        assertEquals(eventDate, item.getEventDate());
    }
}
