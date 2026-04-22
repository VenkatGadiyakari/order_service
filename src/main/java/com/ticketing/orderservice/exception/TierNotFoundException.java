package com.ticketing.orderservice.exception;

import java.util.UUID;

public class TierNotFoundException extends RuntimeException {

    private final UUID tierId;

    public TierNotFoundException(UUID tierId) {
        super("Ticket tier not found with id: " + tierId);
        this.tierId = tierId;
    }

    public UUID getTierId() {
        return tierId;
    }
}
