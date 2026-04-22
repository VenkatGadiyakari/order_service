-- Initialize database with required schemas

-- Create events schema (for cross-schema references)
CREATE SCHEMA IF NOT EXISTS events;

-- Create auth schema (for cross-schema references)
CREATE SCHEMA IF NOT EXISTS auth;

-- Create sample event and tier for testing (optional)
CREATE TABLE IF NOT EXISTS events.events (
    id UUID PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    event_date TIMESTAMP NOT NULL,
    status VARCHAR(30) NOT NULL
);

CREATE TABLE IF NOT EXISTS events.ticket_tiers (
    id UUID PRIMARY KEY,
    event_id UUID NOT NULL,
    name VARCHAR(100) NOT NULL,
    price NUMERIC(10, 2) NOT NULL,
    remaining_qty INTEGER NOT NULL,
    max_per_order INTEGER NOT NULL,
    status VARCHAR(30) NOT NULL
);

CREATE TABLE IF NOT EXISTS auth.users (
    id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    role VARCHAR(30) NOT NULL
);
