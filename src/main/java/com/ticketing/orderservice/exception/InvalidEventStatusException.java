package com.ticketing.orderservice.exception;

public class InvalidEventStatusException extends RuntimeException {

    private final String status;

    public InvalidEventStatusException(String status) {
        super("Event is not available for purchase. Current status: " + status);
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
