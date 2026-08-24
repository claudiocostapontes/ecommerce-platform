-- Create orders table
CREATE TABLE IF NOT EXISTS orders (
                                      id UUID PRIMARY KEY,
                                      order_number VARCHAR(50) NOT NULL UNIQUE,
    user_id UUID NOT NULL,
    status VARCHAR(30) NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL,
    discount DECIMAL(10, 2),
    shipping_cost DECIMAL(10, 2) NOT NULL,
    total DECIMAL(10, 2) NOT NULL,
    delivery_address TEXT,
    delivery_city VARCHAR(100),
    delivery_state VARCHAR(50),
    delivery_zip_code VARCHAR(20),
    delivery_recipient_name VARCHAR(150),
    delivery_phone VARCHAR(20),
    tracking_code VARCHAR(100),
    estimated_delivery TIMESTAMP,
    delivered_at TIMESTAMP,
    customer_notes TEXT,
    admin_notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT,
    FOREIGN KEY (user_id) REFERENCES users(id)
    );

-- Create order_items table
CREATE TABLE IF NOT EXISTS order_items (
                                           id UUID PRIMARY KEY,
                                           order_id UUID NOT NULL,
                                           product_id UUID NOT NULL,
                                           quantity INTEGER NOT NULL,
                                           unit_price DECIMAL(10, 2) NOT NULL,
    product_name VARCHAR(200) NOT NULL,
    product_sku VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    version BIGINT,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id)
    );

-- Create order_status_history table
CREATE TABLE IF NOT EXISTS order_status_history (
                                                    id UUID PRIMARY KEY,
                                                    order_id UUID NOT NULL,
                                                    previous_status VARCHAR(30),
    new_status VARCHAR(30) NOT NULL,
    reason TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
    );

-- Create indexes
CREATE INDEX idx_orders_user_id ON orders(user_id);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_orders_created_at ON orders(created_at DESC);
CREATE INDEX idx_orders_order_number ON orders(order_number);
CREATE INDEX idx_order_items_order_id ON order_items(order_id);
CREATE INDEX idx_order_items_product_id ON order_items(product_id);
CREATE INDEX idx_order_status_history_order_id ON order_status_history(order_id);