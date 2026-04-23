package com.ticketing.orderservice.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AuditServiceTest {

    private AuditService auditService;

    @BeforeEach
    void setUp() {
        auditService = new AuditService();
    }

    @Test
    void testLogOrderCreated() {
        UUID orderId = UUID.randomUUID();
        UUID buyerId = UUID.randomUUID();
        Integer itemCount = 2;
        String totalAmount = "5000.00";

        assertDoesNotThrow(() -> auditService.logOrderCreated(orderId, buyerId, itemCount, totalAmount));
    }

    @Test
    void testLogRazorpayLinkCreated() {
        UUID orderId = UUID.randomUUID();
        String paymentLinkId = "plink_test_123";

        assertDoesNotThrow(() -> auditService.logRazorpayLinkCreated(orderId, paymentLinkId));
    }

    @Test
    void testLogWebhookReceived() {
        String eventId = "pay_test123";
        String eventType = "payment_link.paid";
        UUID orderId = UUID.randomUUID();

        assertDoesNotThrow(() -> auditService.logWebhookReceived(eventId, eventType, orderId));
    }

    @Test
    void testLogOrderConfirmed() {
        UUID orderId = UUID.randomUUID();

        assertDoesNotThrow(() -> auditService.logOrderConfirmed(orderId));
    }

    @Test
    void testLogOrderFailed() {
        UUID orderId = UUID.randomUUID();
        String reason = "Insufficient inventory";

        assertDoesNotThrow(() -> auditService.logOrderFailed(orderId, reason));
    }

    @Test
    void testLogInventoryDecremented() {
        UUID tierId = UUID.randomUUID();
        Integer quantity = 5;

        assertDoesNotThrow(() -> auditService.logInventoryDecremented(tierId, quantity));
    }

    @Test
    void testLogError() {
        String errorCode = "VALIDATION_FAILED";
        String message = "Invalid request";

        assertDoesNotThrow(() -> auditService.logError(errorCode, message));
    }

    @Test
    void testLogInvalidWebhookSignature() {
        String sourceInfo = "razorpay-webhook";

        assertDoesNotThrow(() -> auditService.logInvalidWebhookSignature(sourceInfo));
    }

    @Test
    void testLogOrderCreatedWithNullValues() {
        assertDoesNotThrow(() -> auditService.logOrderCreated(null, null, null, null));
    }

    @Test
    void testLogRazorpayLinkCreatedWithNullValues() {
        assertDoesNotThrow(() -> auditService.logRazorpayLinkCreated(null, null));
    }

    @Test
    void testLogWebhookReceivedWithNullValues() {
        assertDoesNotThrow(() -> auditService.logWebhookReceived(null, null, null));
    }
}
