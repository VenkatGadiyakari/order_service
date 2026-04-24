package com.ticketing.orderservice.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class EventTest {

    @Test
    void testEventCreation() {
        UUID id = UUID.randomUUID();
        String title = "Rock Concert 2026";
        LocalDateTime eventDate = LocalDateTime.now();
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
        LocalDateTime eventDate = LocalDateTime.now();
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
        LocalDateTime eventDate = LocalDateTime.of(2026, 5, 20, 19, 0, 0);
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
