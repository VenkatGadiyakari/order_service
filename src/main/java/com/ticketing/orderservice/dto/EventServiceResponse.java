package com.ticketing.orderservice.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class EventServiceResponse {

    private UUID id;
    private String title;
    private String description;
    private String category;
    private Instant eventDate;
    private String status;
    private List<TierResponse> tiers;

    public EventServiceResponse() {
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Instant getEventDate() { return eventDate; }
    public void setEventDate(Instant eventDate) { this.eventDate = eventDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<TierResponse> getTiers() { return tiers; }
    public void setTiers(List<TierResponse> tiers) { this.tiers = tiers; }

    public static class TierResponse {

        private UUID id;
        private UUID eventId;
        private String name;
        private String description;
        private BigDecimal price;
        private Integer totalQty;
        private Integer remainingQty;
        private Integer maxPerOrder;
        private Instant saleStartsAt;
        private Instant saleEndsAt;
        private String status;

        public TierResponse() {
        }

        public UUID getId() { return id; }
        public void setId(UUID id) { this.id = id; }

        public UUID getEventId() { return eventId; }
        public void setEventId(UUID eventId) { this.eventId = eventId; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }

        public Integer getTotalQty() { return totalQty; }
        public void setTotalQty(Integer totalQty) { this.totalQty = totalQty; }

        public Integer getRemainingQty() { return remainingQty; }
        public void setRemainingQty(Integer remainingQty) { this.remainingQty = remainingQty; }

        public Integer getMaxPerOrder() { return maxPerOrder; }
        public void setMaxPerOrder(Integer maxPerOrder) { this.maxPerOrder = maxPerOrder; }

        public Instant getSaleStartsAt() { return saleStartsAt; }
        public void setSaleStartsAt(Instant saleStartsAt) { this.saleStartsAt = saleStartsAt; }

        public Instant getSaleEndsAt() { return saleEndsAt; }
        public void setSaleEndsAt(Instant saleEndsAt) { this.saleEndsAt = saleEndsAt; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}
