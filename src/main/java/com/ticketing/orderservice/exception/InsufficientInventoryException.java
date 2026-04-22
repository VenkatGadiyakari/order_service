package com.ticketing.orderservice.exception;

public class InsufficientInventoryException extends RuntimeException {

    private final Integer requested;
    private final Integer available;

    public InsufficientInventoryException(Integer requested, Integer available) {
        super("Insufficient tickets available. Requested: " + requested + ", Available: " + available);
        this.requested = requested;
        this.available = available;
    }

    public Integer getRequested() {
        return requested;
    }

    public Integer getAvailable() {
        return available;
    }
}
