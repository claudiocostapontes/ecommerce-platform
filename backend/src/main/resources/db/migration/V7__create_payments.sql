-- Create payments table
CREATE TABLE IF NOT EXISTS payments (
                                        id UUID PRIMARY KEY,
                                        order_id UUID NOT NULL UNIQUE,
                                        method VARCHAR(30) NOT NULL,
    status VARCHAR(30) NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    refunded_amount DECIMAL(10, 2),
    gateway_transaction_id VARCHAR(100),
    gateway_name VARCHAR(50),
    payment_reference VARCHAR(100),
    metadata JSONB,
    paid_at TIMESTAMP,
    failed_at TIMESTAMP,
    failure_reason TEXT,
    idempotency_key VARCHAR(100) UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT,
    FOREIGN KEY (order_id) REFERENCES orders(id)
    );

-- Create indexes
CREATE INDEX idx_payments_order_id ON payments(order_id);
CREATE INDEX idx_payments_status ON payments(status);
CREATE INDEX idx_payments_gateway_id ON payments(gateway_transaction_id);
CREATE INDEX idx_payments_idempotency_key ON payments(idempotency_key);
CREATE INDEX idx_payments_created_at ON payments(created_at DESC);