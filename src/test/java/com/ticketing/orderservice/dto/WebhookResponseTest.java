package com.ticketing.orderservice.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WebhookResponseTest {

    @Test
    void testValidWebhookResponse() {
        WebhookResponse response = new WebhookResponse(true);
        assertTrue(response.getReceived());
    }

    @Test
    void testWebhookResponseFalse() {
        WebhookResponse response = new WebhookResponse(false);
        assertFalse(response.getReceived());
    }

    @Test
    void testSettersAndGetters() {
        WebhookResponse response = new WebhookResponse();
        response.setReceived(true);
        assertTrue(response.getReceived());

        response.setReceived(false);
        assertFalse(response.getReceived());
    }

    @Test
    void testNoArgsConstructor() {
        WebhookResponse response = new WebhookResponse();
        assertNotNull(response);
        assertNull(response.getReceived());
    }

    @Test
    void testAllArgsConstructor() {
        WebhookResponse response = new WebhookResponse(true);
        assertTrue(response.getReceived());
    }
}
