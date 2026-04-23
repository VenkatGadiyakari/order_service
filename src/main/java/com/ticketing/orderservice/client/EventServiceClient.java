package com.ticketing.orderservice.client;

import com.ticketing.orderservice.dto.EventServiceResponse;
import com.ticketing.orderservice.exception.EventNotFoundException;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Optional;
import java.util.UUID;

@Component
public class EventServiceClient {

    private final RestClient restClient;

    public EventServiceClient(RestClient eventRestClient) {
        this.restClient = eventRestClient;
    }

    public Optional<EventServiceResponse> getEvent(UUID eventId) {
        EventServiceResponse response = restClient.get()
                .uri("/api/events/{id}", eventId)
                .retrieve()
                .onStatus(status -> status.value() == 404,
                        (req, res) -> { throw new EventNotFoundException(eventId); })
                .onStatus(HttpStatusCode::isError,
                        (req, res) -> { throw new RuntimeException(
                                "Event service error: " + res.getStatusCode()); })
                .body(EventServiceResponse.class);

        return Optional.ofNullable(response);
    }
}
