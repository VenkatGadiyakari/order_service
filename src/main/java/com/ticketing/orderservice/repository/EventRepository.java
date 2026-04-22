package com.ticketing.orderservice.repository;

import com.ticketing.orderservice.util.Event;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class EventRepository {

    private final JdbcTemplate jdbcTemplate;

    public EventRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<Event> findById(UUID eventId) {
        String sql = "SELECT id, title, event_date, status FROM events.events WHERE id = ?";

        List<Event> results = jdbcTemplate.query(sql, new EventRowMapper(), eventId);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    private static class EventRowMapper implements RowMapper<Event> {
        @Override
        public Event mapRow(ResultSet rs, int rowNum) throws SQLException {
            Event event = new Event();
            event.setId(UUID.fromString(rs.getString("id")));
            event.setTitle(rs.getString("title"));
            Timestamp timestamp = rs.getTimestamp("event_date");
            event.setEventDate(timestamp != null ? timestamp.toInstant() : null);
            event.setStatus(rs.getString("status"));
            return event;
        }
    }
}
