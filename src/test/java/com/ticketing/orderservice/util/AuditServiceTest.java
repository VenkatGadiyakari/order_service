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
    void testLogWebhookReceived() {
        String eventId = "pay_test123";
        String eventType = "payment.captured";
        UUID orderId = UUID.randomUUID();

        assertDoesNotThrow(() -> auditService.logWebhookReceived(eventId, eventType, orderId));
    }

    @Test
    void testLogOrderConfirmed() {
        UUID orderId = UUID.randomUUID();

        assertDoesNotThrow(() -> auditService.logOrderConfirmed(orderId));
    }

    @Test
    void testLogOrderConfirmedByPayment() {
        UUID orderId = UUID.randomUUID();
        String paymentRef = "pay_test_123456";

        assertDoesNotThrow(() -> auditService.logOrderConfirmedByPayment(orderId, paymentRef));
    }

    @Test
    void testLogOrderFailed() {
        UUID orderId = UUID.randomUUID();
        String reason = "Insufficient inventory";

        assertDoesNotThrow(() -> auditService.logOrderFailed(orderId, reason));
    }

    @Test
    void testLogOrderFailedByPayment() {
        UUID orderId = UUID.randomUUID();
        String reason = "Payment failed";

        assertDoesNotThrow(() -> auditService.logOrderFailedByPayment(orderId, reason));
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
    void testLogWebhookReceivedWithNullValues() {
        assertDoesNotThrow(() -> auditService.logWebhookReceived(null, null, null));
    }
}
