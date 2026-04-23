ALTER TABLE orders.orders RENAME COLUMN stripe_session_id TO razorpay_payment_link_id;

DROP INDEX IF EXISTS idx_orders_stripe_session_id;
CREATE INDEX idx_orders_razorpay_payment_link_id ON orders.orders(razorpay_payment_link_id);
