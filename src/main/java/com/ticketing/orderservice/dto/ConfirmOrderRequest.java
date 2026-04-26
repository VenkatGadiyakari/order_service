package com.ticketing.orderservice.dto;

public class ConfirmOrderRequest {

    private String paymentReferenceId;

    public ConfirmOrderRequest() {
    }

    public ConfirmOrderRequest(String paymentReferenceId) {
        this.paymentReferenceId = paymentReferenceId;
    }

    public String getPaymentReferenceId() {
        return paymentReferenceId;
    }

    public void setPaymentReferenceId(String paymentReferenceId) {
        this.paymentReferenceId = paymentReferenceId;
    }
}
