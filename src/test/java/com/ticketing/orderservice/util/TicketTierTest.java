package com.ticketing.orderservice.util;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TicketTierTest {

    @Test
    void testTicketTierCreation() {
        UUID id = UUID.randomUUID();
        UUID eventId = UUID.randomUUID();
        String name = "VIP";
        BigDecimal price = new BigDecimal("2500.00");
        Integer remainingQty = 100;
        Integer maxPerOrder = 5;
        String status = "ACTIVE";

        TicketTier tier = new TicketTier(id, eventId, name, price, remainingQty, maxPerOrder, status);

        assertEquals(id, tier.getId());
        assertEquals(eventId, tier.getEventId());
        assertEquals(name, tier.getName());
        assertEquals(price, tier.getPrice());
        assertEquals(remainingQty, tier.getRemainingQty());
        assertEquals(maxPerOrder, tier.getMaxPerOrder());
        assertEquals(status, tier.getStatus());
    }

    @Test
    void testTicketTierSettersAndGetters() {
        TicketTier tier = new TicketTier();
        UUID id = UUID.randomUUID();
        UUID eventId = UUID.randomUUID();
        String name = "Standard";
        BigDecimal price = new BigDecimal("1000.00");
        Integer remainingQty = 50;
        Integer maxPerOrder = 10;
        String status = "ACTIVE";

        tier.setId(id);
        tier.setEventId(eventId);
        tier.setName(name);
        tier.setPrice(price);
        tier.setRemainingQty(remainingQty);
        tier.setMaxPerOrder(maxPerOrder);
        tier.setStatus(status);

        assertEquals(id, tier.getId());
        assertEquals(eventId, tier.getEventId());
        assertEquals(name, tier.getName());
        assertEquals(price, tier.getPrice());
        assertEquals(remainingQty, tier.getRemainingQty());
        assertEquals(maxPerOrder, tier.getMaxPerOrder());
        assertEquals(status, tier.getStatus());
    }

    @Test
    void testNoArgsConstructor() {
        TicketTier tier = new TicketTier();
        assertNotNull(tier);
        assertNull(tier.getId());
        assertNull(tier.getEventId());
        assertNull(tier.getName());
        assertNull(tier.getPrice());
        assertNull(tier.getRemainingQty());
        assertNull(tier.getMaxPerOrder());
        assertNull(tier.getStatus());
    }

    @Test
    void testTicketTierWithZeroRemainingQty() {
        TicketTier tier = new TicketTier();
        tier.setRemainingQty(0);
        assertEquals(0, tier.getRemainingQty());
    }

    @Test
    void testTicketTierPricePrecision() {
        TicketTier tier = new TicketTier();
        BigDecimal price = new BigDecimal("1234.56");
        tier.setPrice(price);
        assertEquals(price, tier.getPrice());
        assertEquals(2, tier.getPrice().scale());
    }

    @Test
    void testTicketTierStatusValues() {
        TicketTier tier = new TicketTier();
        tier.setStatus("ACTIVE");
        assertEquals("ACTIVE", tier.getStatus());

        tier.setStatus("CLOSED");
        assertEquals("CLOSED", tier.getStatus());
    }
}
