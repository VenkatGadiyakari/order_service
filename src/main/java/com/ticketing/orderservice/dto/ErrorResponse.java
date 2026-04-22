package com.ticketing.orderservice.dto;

import java.time.Instant;

public class ErrorResponse {

    private String errorCode;
    private String message;
    private Instant timestamp;
    private String traceId;

    public ErrorResponse() {
    }

    public ErrorResponse(String errorCode, String message, Instant timestamp, String traceId) {
        this.errorCode = errorCode;
        this.message = message;
        this.timestamp = timestamp;
        this.traceId = traceId;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }
}
