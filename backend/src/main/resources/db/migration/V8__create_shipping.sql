-- Create shipments table
CREATE TABLE IF NOT EXISTS shipments (
                                         id UUID PRIMARY KEY,
                                         order_id UUID NOT NULL UNIQUE,
                                         tracking_code VARCHAR(100) UNIQUE,
    carrier VARCHAR(50) NOT NULL,
    shipping_method VARCHAR(100) NOT NULL,
    cost DECIMAL(10, 2),
    estimated_delivery TIMESTAMP,
    actual_delivery TIMESTAMP,
    sender_name VARCHAR(150),
    sender_address TEXT,
    recipient_name VARCHAR(150),
    recipient_address TEXT,
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT,
    FOREIGN KEY (order_id) REFERENCES orders(id)
    );

-- Create indexes
CREATE INDEX idx_shipments_order_id ON shipments(order_id);
CREATE INDEX idx_shipments_tracking_code ON shipments(tracking_code);
CREATE INDEX idx_shipments_estimated_delivery ON shipments(estimated_delivery);