-- Create carts table
CREATE TABLE IF NOT EXISTS carts (
                                     id UUID PRIMARY KEY,
                                     user_id UUID,
                                     session_id VARCHAR(100),
    last_activity TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT,
    FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT chk_cart_identifier CHECK (user_id IS NOT NULL OR session_id IS NOT NULL)
    );

-- Create cart_items table
CREATE TABLE IF NOT EXISTS cart_items (
                                          id UUID PRIMARY KEY,
                                          cart_id UUID NOT NULL,
                                          product_id UUID NOT NULL,
                                          quantity INTEGER NOT NULL,
                                          unit_price DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    version BIGINT,
    FOREIGN KEY (cart_id) REFERENCES carts(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT chk_cart_item_quantity CHECK (quantity > 0)
    );

-- Create indexes
CREATE INDEX idx_carts_user_id ON carts(user_id);
CREATE INDEX idx_carts_session_id ON carts(session_id);
CREATE INDEX idx_carts_last_activity ON carts(last_activity);
CREATE INDEX idx_cart_items_cart_id ON cart_items(cart_id);
CREATE INDEX idx_cart_items_product_id ON cart_items(product_id);