package com.ticketing.orderservice.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class OrderItemDetail {

    private UUID orderItemId;
    private UUID tierId;
    private String tierName;
    private String eventTitle;
    private LocalDateTime eventDate;
    private Integer quantity;
    private BigDecimal unitPrice;
    private LocalDateTime createdAt;

    public OrderItemDetail() {
    }

    public OrderItemDetail(UUID orderItemId, UUID tierId, String tierName, String eventTitle, LocalDateTime eventDate, Integer quantity, BigDecimal unitPrice, LocalDateTime createdAt) {
        this.orderItemId = orderItemId;
        this.tierId = tierId;
        this.tierName = tierName;
        this.eventTitle = eventTitle;
        this.eventDate = eventDate;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.createdAt = createdAt;
    }

    public UUID getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(UUID orderItemId) {
        this.orderItemId = orderItemId;
    }

    public UUID getTierId() {
        return tierId;
    }

    public void setTierId(UUID tierId) {
        this.tierId = tierId;
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

    public LocalDateTime getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDateTime eventDate) {
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
