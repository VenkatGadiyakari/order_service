package com.ticketing.orderservice.dto;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class ErrorResponseTest {

    @Test
    void testValidErrorResponse() {
        String errorCode = "VALIDATION_FAILED";
        String message = "Invalid request";
        Instant timestamp = Instant.now();
        String traceId = "trace-123";

        ErrorResponse response = new ErrorResponse(errorCode, message, timestamp, traceId);

        assertEquals(errorCode, response.getErrorCode());
        assertEquals(message, response.getMessage());
        assertEquals(timestamp, response.getTimestamp());
        assertEquals(traceId, response.getTraceId());
    }

    @Test
    void testSettersAndGetters() {
        ErrorResponse response = new ErrorResponse();
        String errorCode = "NOT_FOUND";
        String message = "Resource not found";
        Instant timestamp = Instant.now();
        String traceId = "trace-456";

        response.setErrorCode(errorCode);
        response.setMessage(message);
        response.setTimestamp(timestamp);
        response.setTraceId(traceId);

        assertEquals(errorCode, response.getErrorCode());
        assertEquals(message, response.getMessage());
        assertEquals(timestamp, response.getTimestamp());
        assertEquals(traceId, response.getTraceId());
    }

    @Test
    void testNoArgsConstructor() {
        ErrorResponse response = new ErrorResponse();
        assertNotNull(response);
        assertNull(response.getErrorCode());
        assertNull(response.getMessage());
        assertNull(response.getTimestamp());
        assertNull(response.getTraceId());
    }

    @Test
    void testAllArgsConstructor() {
        String errorCode = "INTERNAL_ERROR";
        String message = "Server error";
        Instant timestamp = Instant.now();
        String traceId = "trace-789";

        ErrorResponse response = new ErrorResponse(errorCode, message, timestamp, traceId);

        assertEquals(errorCode, response.getErrorCode());
        assertEquals(message, response.getMessage());
        assertEquals(timestamp, response.getTimestamp());
        assertEquals(traceId, response.getTraceId());
    }
}
