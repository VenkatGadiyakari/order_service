package com.ticketing.orderservice.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class OrderItemSummary {

    private UUID orderItemId;
    private String tierName;
    private String eventTitle;
    private Instant eventDate;
    private Integer quantity;
    private BigDecimal unitPrice;

    public OrderItemSummary() {
    }

    public OrderItemSummary(UUID orderItemId, String tierName, String eventTitle, Instant eventDate, Integer quantity, BigDecimal unitPrice) {
        this.orderItemId = orderItemId;
        this.tierName = tierName;
        this.eventTitle = eventTitle;
        this.eventDate = eventDate;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public UUID getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(UUID orderItemId) {
        this.orderItemId = orderItemId;
    }

    public String getTierName() {
        return tierName;
    }

    public void setTierName(String tierName) {
        this.tierName = tierName;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public Instant getEventDate() {
        return eventDate;
    }

    public void setEventDate(Instant eventDate) {
        this.eventDate = eventDate;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }
}
