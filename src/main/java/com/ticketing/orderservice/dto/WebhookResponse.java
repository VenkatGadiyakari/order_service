package com.ticketing.orderservice.dto;

public class WebhookResponse {

    private Boolean received;

    public WebhookResponse() {
    }

    public WebhookResponse(Boolean received) {
        this.received = received;
    }

    public Boolean getReceived() {
        return received;
    }

    public void setReceived(Boolean received) {
        this.received = received;
    }
}
