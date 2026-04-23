package com.ticketing.orderservice.util;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class TicketTier {

    private UUID id;
    private UUID eventId;
    private String name;
    private BigDecimal price;
    private Integer remainingQty;
    private Integer maxPerOrder;
    private String status;
    private Instant saleStartsAt;
    private Instant saleEndsAt;

    public TicketTier() {
    }

    public TicketTier(UUID id, UUID eventId, String name, BigDecimal price, Integer remainingQty, Integer maxPerOrder, String status) {
        this.id = id;
        this.eventId = eventId;
        this.name = name;
        this.price = price;
        this.remainingQty = remainingQty;
        this.maxPerOrder = maxPerOrder;
        this.status = status;
    }

    public TicketTier(UUID id, UUID eventId, String name, BigDecimal price, Integer remainingQty, Integer maxPerOrder, String status, Instant saleStartsAt, Instant saleEndsAt) {
        this.id = id;
        this.eventId = eventId;
        this.name = name;
        this.price = price;
        this.remainingQty = remainingQty;
        this.maxPerOrder = maxPerOrder;
        this.status = status;
        this.saleStartsAt = saleStartsAt;
        this.saleEndsAt = saleEndsAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getEventId() {
        return eventId;
    }

    public void setEventId(UUID eventId) {
        this.eventId = eventId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getRemainingQty() {
        return remainingQty;
    }

    public void setRemainingQty(Integer remainingQty) {
        this.remainingQty = remainingQty;
    }

    public Integer getMaxPerOrder() {
        return maxPerOrder;
    }

    public void setMaxPerOrder(Integer maxPerOrder) {
        this.maxPerOrder = maxPerOrder;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getSaleStartsAt() {
        return saleStartsAt;
    }

    public void setSaleStartsAt(Instant saleStartsAt) {
        this.saleStartsAt = saleStartsAt;
    }

    public Instant getSaleEndsAt() {
        return saleEndsAt;
    }

    public void setSaleEndsAt(Instant saleEndsAt) {
        this.saleEndsAt = saleEndsAt;
    }
}
