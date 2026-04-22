-- Create orders schema
CREATE SCHEMA IF NOT EXISTS orders;

-- Create orders table
CREATE TABLE IF NOT EXISTS orders.orders (
    id UUID PRIMARY KEY,
    buyer_id UUID NOT NULL,
    status VARCHAR(30) NOT NULL CHECK (status IN ('PENDING', 'CONFIRMED', 'FAILED')),
    total_amount NUMERIC(10, 2) NOT NULL,
    stripe_session_id TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Create order_items table
CREATE TABLE IF NOT EXISTS orders.order_items (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL,
    tier_id UUID NOT NULL,
    tier_name VARCHAR(100) NOT NULL,
    event_title VARCHAR(255) NOT NULL,
    event_date TIMESTAMP NOT NULL,
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    unit_price NUMERIC(10, 2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES orders.orders(id) ON DELETE CASCADE
);

-- Create indexes
CREATE INDEX idx_orders_buyer_id ON orders.orders(buyer_id);
CREATE INDEX idx_orders_status ON orders.orders(status);
CREATE INDEX idx_orders_created_at ON orders.orders(created_at);
CREATE INDEX idx_orders_stripe_session_id ON orders.orders(stripe_session_id);
CREATE INDEX idx_order_items_order_id ON orders.order_items(order_id);
CREATE INDEX idx_order_items_tier_id ON orders.order_items(tier_id);

