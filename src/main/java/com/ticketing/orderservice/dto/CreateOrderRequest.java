package com.ticketing.orderservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.UUID;

public class CreateOrderRequest {

    @NotNull(message = "eventId is required")
    private UUID eventId;

    @NotNull(message = "items must not be empty")
    @Size(min = 1, message = "items must not be empty")
    private List<@Valid OrderItemRequest> items;

    public CreateOrderRequest() {
    }

    public CreateOrderRequest(UUID eventId, List<OrderItemRequest> items) {
        this.eventId = eventId;
        this.items = items;
    }

    public UUID getEventId() {
        return eventId;
    }

    public void setEventId(UUID eventId) {
        this.eventId = eventId;
    }

    public List<OrderItemRequest> getItems() {
        return items;
    }

    public void setItems(List<OrderItemRequest> items) {
        this.items = items;
    }
}
