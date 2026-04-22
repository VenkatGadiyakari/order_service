package com.ticketing.orderservice.util;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class EventTest {

    @Test
    void testEventCreation() {
        UUID id = UUID.randomUUID();
        String title = "Rock Concert 2026";
        Instant eventDate = Instant.now();
        String status = "PUBLISHED";

        Event event = new Event(id, title, eventDate, status);

        assertEquals(id, event.getId());
        assertEquals(title, event.getTitle());
        assertEquals(eventDate, event.getEventDate());
        assertEquals(status, event.getStatus());
    }

    @Test
    void testEventSettersAndGetters() {
        Event event = new Event();
        UUID id = UUID.randomUUID();
        String title = "Jazz Night";
        Instant eventDate = Instant.now();
        String status = "PUBLISHED";

        event.setId(id);
        event.setTitle(title);
        event.setEventDate(eventDate);
        event.setStatus(status);

        assertEquals(id, event.getId());
        assertEquals(title, event.getTitle());
        assertEquals(eventDate, event.getEventDate());
        assertEquals(status, event.getStatus());
    }

    @Test
    void testNoArgsConstructor() {
        Event event = new Event();
        assertNotNull(event);
        assertNull(event.getId());
        assertNull(event.getTitle());
        assertNull(event.getEventDate());
        assertNull(event.getStatus());
    }

    @Test
    void testEventDateStorage() {
        Event event = new Event();
        Instant eventDate = Instant.parse("2026-05-20T19:00:00Z");
        event.setEventDate(eventDate);
        assertEquals(eventDate, event.getEventDate());
    }

    @Test
    void testEventStatusValues() {
        Event event = new Event();
        event.setStatus("PUBLISHED");
        assertEquals("PUBLISHED", event.getStatus());

        event.setStatus("CANCELLED");
        assertEquals("CANCELLED", event.getStatus());

        event.setStatus("DRAFT");
        assertEquals("DRAFT", event.getStatus());
    }

    @Test
    void testEventWithLongTitle() {
        Event event = new Event();
        String longTitle = "A".repeat(255);
        event.setTitle(longTitle);
        assertEquals(255, event.getTitle().length());
    }
}
