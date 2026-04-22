package com.ticketing.orderservice.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderStatusTest {

    @Test
    void testOrderStatusValues() {
        assertEquals(3, OrderStatus.values().length);
        assertEquals("PENDING", OrderStatus.PENDING.name());
        assertEquals("CONFIRMED", OrderStatus.CONFIRMED.name());
        assertEquals("FAILED", OrderStatus.FAILED.name());
    }

    @Test
    void testOrderStatusValueOf() {
        assertEquals(OrderStatus.PENDING, OrderStatus.valueOf("PENDING"));
        assertEquals(OrderStatus.CONFIRMED, OrderStatus.valueOf("CONFIRMED"));
        assertEquals(OrderStatus.FAILED, OrderStatus.valueOf("FAILED"));
    }

    @Test
    void testOrderStatusInvalidValueOf() {
        assertThrows(IllegalArgumentException.class, () -> OrderStatus.valueOf("INVALID"));
    }

    @Test
    void testOrderStatusEquality() {
        OrderStatus status1 = OrderStatus.PENDING;
        OrderStatus status2 = OrderStatus.PENDING;
        OrderStatus status3 = OrderStatus.CONFIRMED;

        assertEquals(status1, status2);
        assertNotEquals(status1, status3);
    }

    @Test
    void testOrderStatusOrdinal() {
        assertEquals(0, OrderStatus.PENDING.ordinal());
        assertEquals(1, OrderStatus.CONFIRMED.ordinal());
        assertEquals(2, OrderStatus.FAILED.ordinal());
    }

    @Test
    void testOrderStatusComparison() {
        assertTrue(OrderStatus.PENDING.ordinal() < OrderStatus.CONFIRMED.ordinal());
        assertTrue(OrderStatus.CONFIRMED.ordinal() < OrderStatus.FAILED.ordinal());
    }
}
