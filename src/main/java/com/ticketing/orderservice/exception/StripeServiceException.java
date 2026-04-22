package com.ticketing.orderservice.exception;

public class StripeServiceException extends RuntimeException {

    public StripeServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public StripeServiceException(String message) {
        super(message);
    }
}
