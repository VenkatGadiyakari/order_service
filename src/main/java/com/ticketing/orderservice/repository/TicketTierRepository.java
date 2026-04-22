package com.ticketing.orderservice.repository;

import com.ticketing.orderservice.exception.TierNotFoundException;
import com.ticketing.orderservice.util.TicketTier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class TicketTierRepository {

    private final JdbcTemplate jdbcTemplate;

    public TicketTierRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<TicketTier> findById(UUID tierId) {
        String sql = "SELECT id, event_id, name, price, remaining_qty, max_per_order, status " +
                     "FROM events.ticket_tiers WHERE id = ?";

        List<TicketTier> results = jdbcTemplate.query(sql, new TicketTierRowMapper(), tierId);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public int decrementRemainingQty(UUID tierId, Integer quantity) {
        String sql = "UPDATE events.ticket_tiers " +
                     "SET remaining_qty = remaining_qty - ? " +
                     "WHERE id = ? AND remaining_qty >= ?";

        return jdbcTemplate.update(sql, quantity, tierId, quantity);
    }

    private static class TicketTierRowMapper implements RowMapper<TicketTier> {
        @Override
        public TicketTier mapRow(ResultSet rs, int rowNum) throws SQLException {
            TicketTier tier = new TicketTier();
            tier.setId(UUID.fromString(rs.getString("id")));
            tier.setEventId(UUID.fromString(rs.getString("event_id")));
            tier.setName(rs.getString("name"));
            tier.setPrice(rs.getBigDecimal("price"));
            tier.setRemainingQty(rs.getInt("remaining_qty"));
            tier.setMaxPerOrder(rs.getInt("max_per_order"));
            tier.setStatus(rs.getString("status"));
            return tier;
        }
    }
}
