package com.ticketing.orderservice.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class TicketTierRepository {

    private final JdbcTemplate jdbcTemplate;

    public TicketTierRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int decrementRemainingQty(UUID tierId, Integer quantity) {
        String sql = "UPDATE events.ticket_tiers " +
                     "SET remaining_qty = remaining_qty - ? " +
                     "WHERE id = ? AND remaining_qty >= ?";

        return jdbcTemplate.update(sql, quantity, tierId, quantity);
    }
}
