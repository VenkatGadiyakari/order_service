package com.ticketing.orderservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.UUID;

@Schema(description = "Request body for placing a ticket order")
public class CreateOrderRequest {

    @Schema(description = "ID of the event to purchase tickets for", example = "c3d4e5f6-a7b8-9012-cdef-123456789012")
    @NotNull(message = "eventId is required")
    private UUID eventId;

    @Schema(description = "List of ticket tiers and quantities to purchase (at least one item required)")
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
