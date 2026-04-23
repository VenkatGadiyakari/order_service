package com.ticketing.orderservice.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuditService {

    private static final Logger auditLogger = LoggerFactory.getLogger("AUDIT");

    public void logOrderCreated(UUID orderId, UUID buyerId, Integer itemCount, String totalAmount) {
        auditLogger.info("ORDER_CREATED - orderId={}, buyerId={}, itemCount={}, totalAmount={}",
                maskUuid(orderId), maskUuid(buyerId), itemCount, maskAmount(totalAmount));
    }

    public void logRazorpayLinkCreated(UUID orderId, String paymentLinkId) {
        auditLogger.info("RAZORPAY_LINK_CREATED - orderId={}, paymentLinkId={}",
                maskUuid(orderId), maskSensitiveData(paymentLinkId));
    }

    public void logWebhookReceived(String eventId, String eventType, UUID orderId) {
        auditLogger.info("WEBHOOK_RECEIVED - eventId={}, eventType={}, orderId={}",
                eventId, eventType, maskUuid(orderId));
    }

    public void logOrderConfirmed(UUID orderId) {
        auditLogger.info("ORDER_CONFIRMED - orderId={}", maskUuid(orderId));
    }

    public void logOrderFailed(UUID orderId, String reason) {
        auditLogger.info("ORDER_FAILED - orderId={}, reason={}", maskUuid(orderId), reason);
    }

    public void logInventoryDecremented(UUID tierId, Integer quantity) {
        auditLogger.info("INVENTORY_DECREMENTED - tierId={}, quantity={}", maskUuid(tierId), quantity);
    }

    public void logError(String errorCode, String message) {
        auditLogger.error("ERROR - errorCode={}, message={}", errorCode, message);
    }

    public void logInvalidWebhookSignature(String sourceInfo) {
        auditLogger.warn("INVALID_WEBHOOK_SIGNATURE - source={}", sourceInfo);
    }

    private String maskUuid(UUID uuid) {
        if (uuid == null) {
            return "null";
        }
        String uuidStr = uuid.toString();
        return uuidStr.substring(0, 8) + "****";
    }

    private String maskAmount(String amount) {
        return "***";
    }

    private String maskSensitiveData(String data) {
        if (data == null || data.length() < 10) {
            return "****";
        }
        return data.substring(0, 10) + "****";
    }
}
