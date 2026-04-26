package com.ticketing.orderservice.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class OrderDetailResponse {

    private UUID orderId;
    private String status;
    private BigDecimal totalAmount;
    private String paymentReferenceId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<OrderItemDetail> items;

    public OrderDetailResponse() {
    }

    public OrderDetailResponse(UUID orderId, String status, BigDecimal totalAmount, String paymentReferenceId, LocalDateTime createdAt, LocalDateTime updatedAt, List<OrderItemDetail> items) {
        this.orderId = orderId;
        this.status = status;
        this.totalAmount = totalAmount;
        this.paymentReferenceId = paymentReferenceId;
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

    public String getPaymentReferenceId() {
        return paymentReferenceId;
    }

    public void setPaymentReferenceId(String paymentReferenceId) {
        this.paymentReferenceId = paymentReferenceId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<OrderItemDetail> getItems() {
        return items;
    }

    public void setItems(List<OrderItemDetail> items) {
        this.items = items;
    }
}
