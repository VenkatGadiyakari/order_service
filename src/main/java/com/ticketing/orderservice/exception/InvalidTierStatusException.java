package com.ticketing.orderservice.exception;

public class InvalidTierStatusException extends RuntimeException {

    private final String status;

    public InvalidTierStatusException(String status) {
        super("Ticket tier is not active. Current status: " + status);
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
