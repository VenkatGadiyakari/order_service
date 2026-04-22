package com.ticketing.orderservice.util;

import java.time.Instant;
import java.util.UUID;

public class Event {

    private UUID id;
    private String title;
    private Instant eventDate;
    private String status;

    public Event() {
    }

    public Event(UUID id, String title, Instant eventDate, String status) {
        this.id = id;
        this.title = title;
        this.eventDate = eventDate;
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Instant getEventDate() {
        return eventDate;
    }

    public void setEventDate(Instant eventDate) {
        this.eventDate = eventDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
