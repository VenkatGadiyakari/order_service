package com.ticketing.orderservice.exception;

public class QuantityExceedsMaxPerOrderException extends RuntimeException {

    private final Integer quantity;
    private final Integer maxPerOrder;

    public QuantityExceedsMaxPerOrderException(Integer quantity, Integer maxPerOrder) {
        super("Quantity exceeds maximum allowed per order. Requested: " + quantity + ", Maximum: " + maxPerOrder);
        this.quantity = quantity;
        this.maxPerOrder = maxPerOrder;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Integer getMaxPerOrder() {
        return maxPerOrder;
    }
}
