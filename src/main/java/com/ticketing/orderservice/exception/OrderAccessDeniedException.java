package com.ticketing.orderservice.exception;

import java.util.UUID;

public class OrderAccessDeniedException extends RuntimeException {

    private final UUID orderId;

    public OrderAccessDeniedException(UUID orderId) {
        super("Access denied to order with id: " + orderId);
        this.orderId = orderId;
    }

    public UUID getOrderId() {
        return orderId;
    }
}
