ALTER TABLE orders.orders RENAME COLUMN razorpay_payment_link_id TO payment_reference_id;
DROP INDEX IF EXISTS idx_orders_razorpay_payment_link_id;
CREATE INDEX idx_orders_payment_reference_id ON orders.orders(payment_reference_id);
