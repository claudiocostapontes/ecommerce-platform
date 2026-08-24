-- Create coupons table
CREATE TABLE IF NOT EXISTS coupons (
                                       id UUID PRIMARY KEY,
                                       code VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    discount_type VARCHAR(30) NOT NULL,
    discount_value DECIMAL(10, 2) NOT NULL,
    max_discount DECIMAL(10, 2),
    min_purchase DECIMAL(10, 2),
    valid_from TIMESTAMP NOT NULL,
    valid_until TIMESTAMP NOT NULL,
    max_uses INTEGER,
    current_uses INTEGER DEFAULT 0,
    max_uses_per_customer INTEGER,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT
    );

-- Create indexes
CREATE INDEX idx_coupons_code ON coupons(code);
CREATE INDEX idx_coupons_active ON coupons(active);
CREATE INDEX idx_coupons_valid_from ON coupons(valid_from);
CREATE INDEX idx_coupons_valid_until ON coupons(valid_until);