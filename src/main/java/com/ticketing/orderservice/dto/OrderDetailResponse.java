package com.ticketing.orderservice.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class OrderDetailResponse {

    private UUID orderId;
    private String status;
    private BigDecimal totalAmount;
    private String razorpayPaymentLinkId;
    private Instant createdAt;
    private Instant updatedAt;
    private List<OrderItemDetail> items;

    public OrderDetailResponse() {
    }

    public OrderDetailResponse(UUID orderId, String status, BigDecimal totalAmount, String razorpayPaymentLinkId, Instant createdAt, Instant updatedAt, List<OrderItemDetail> items) {
        this.orderId = orderId;
        this.status = status;
        this.totalAmount = totalAmount;
        this.razorpayPaymentLinkId = razorpayPaymentLinkId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.items = items;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getRazorpayPaymentLinkId() {
        return razorpayPaymentLinkId;
    }

    public void setRazorpayPaymentLinkId(String razorpayPaymentLinkId) {
        this.razorpayPaymentLinkId = razorpayPaymentLinkId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<OrderItemDetail> getItems() {
        return items;
    }

    public void setItems(List<OrderItemDetail> items) {
        this.items = items;
    }
}
