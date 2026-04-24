package com.ticketing.orderservice.dto;

public class FailOrderRequest {

    private String reason;

    public FailOrderRequest() {
    }

    public FailOrderRequest(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
