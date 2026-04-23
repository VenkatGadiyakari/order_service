package com.ticketing.orderservice.exception;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ExceptionsTest {

    @Test
    void testOrderNotFoundException() {
        UUID orderId = UUID.randomUUID();
        OrderNotFoundException exception = new OrderNotFoundException(orderId);

        assertEquals(orderId, exception.getOrderId());
        assertTrue(exception.getMessage().contains(orderId.toString()));
    }

    @Test
    void testTierNotFoundException() {
        UUID tierId = UUID.randomUUID();
        TierNotFoundException exception = new TierNotFoundException(tierId);

        assertEquals(tierId, exception.getTierId());
        assertTrue(exception.getMessage().contains(tierId.toString()));
    }

    @Test
    void testInsufficientInventoryException() {
        Integer requested = 5;
        Integer available = 2;
        InsufficientInventoryException exception = new InsufficientInventoryException(requested, available);

        assertEquals(requested, exception.getRequested());
        assertEquals(available, exception.getAvailable());
        assertTrue(exception.getMessage().contains("5"));
        assertTrue(exception.getMessage().contains("2"));
    }

    @Test
    void testInvalidTierStatusException() {
        String status = "CLOSED";
        InvalidTierStatusException exception = new InvalidTierStatusException(status);

        assertEquals(status, exception.getStatus());
        assertTrue(exception.getMessage().contains(status));
    }

    @Test
    void testInvalidEventStatusException() {
        String status = "CANCELLED";
        InvalidEventStatusException exception = new InvalidEventStatusException(status);

        assertEquals(status, exception.getStatus());
        assertTrue(exception.getMessage().contains(status));
    }

    @Test
    void testQuantityExceedsMaxPerOrderException() {
        Integer quantity = 10;
        Integer maxPerOrder = 5;
        QuantityExceedsMaxPerOrderException exception = new QuantityExceedsMaxPerOrderException(quantity, maxPerOrder);

        assertEquals(quantity, exception.getQuantity());
        assertEquals(maxPerOrder, exception.getMaxPerOrder());
        assertTrue(exception.getMessage().contains("10"));
        assertTrue(exception.getMessage().contains("5"));
    }

    @Test
    void testOrderAccessDeniedException() {
        UUID orderId = UUID.randomUUID();
        OrderAccessDeniedException exception = new OrderAccessDeniedException(orderId);

        assertEquals(orderId, exception.getOrderId());
        assertTrue(exception.getMessage().contains(orderId.toString()));
    }

    @Test
    void testUnauthorizedException() {
        String message = "Invalid JWT token";
        UnauthorizedException exception = new UnauthorizedException(message);

        assertEquals(message, exception.getMessage());
    }

    @Test
    void testPaymentServiceExceptionWithCause() {
        String message = "Razorpay API error";
        Exception cause = new RuntimeException("Network timeout");
        PaymentServiceException exception = new PaymentServiceException(message, cause);

        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testPaymentServiceExceptionWithoutCause() {
        String message = "Payment processing failed";
        PaymentServiceException exception = new PaymentServiceException(message);

        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testExceptionInheritance() {
        assertTrue(OrderNotFoundException.class.getSuperclass().equals(RuntimeException.class));
        assertTrue(TierNotFoundException.class.getSuperclass().equals(RuntimeException.class));
        assertTrue(InsufficientInventoryException.class.getSuperclass().equals(RuntimeException.class));
        assertTrue(InvalidTierStatusException.class.getSuperclass().equals(RuntimeException.class));
        assertTrue(InvalidEventStatusException.class.getSuperclass().equals(RuntimeException.class));
        assertTrue(QuantityExceedsMaxPerOrderException.class.getSuperclass().equals(RuntimeException.class));
        assertTrue(OrderAccessDeniedException.class.getSuperclass().equals(RuntimeException.class));
        assertTrue(UnauthorizedException.class.getSuperclass().equals(RuntimeException.class));
        assertTrue(PaymentServiceException.class.getSuperclass().equals(RuntimeException.class));
    }

    @Test
    void testInsufficientInventoryWithZeroAvailable() {
        InsufficientInventoryException exception = new InsufficientInventoryException(5, 0);
        assertEquals(5, exception.getRequested());
        assertEquals(0, exception.getAvailable());
    }

    @Test
    void testQuantityExceedsWithLargeNumbers() {
        QuantityExceedsMaxPerOrderException exception = new QuantityExceedsMaxPerOrderException(1000, 100);
        assertEquals(1000, exception.getQuantity());
        assertEquals(100, exception.getMaxPerOrder());
    }
}
