-- Create customers table
CREATE TABLE IF NOT EXISTS customers (
                                         id UUID PRIMARY KEY,
                                         user_id UUID NOT NULL UNIQUE,
                                         cpf VARCHAR(20),
    birth_date DATE,
    phone VARCHAR(20),
    newsletter_subscribed BOOLEAN DEFAULT FALSE,
    marketing_notifications BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT,
    FOREIGN KEY (user_id) REFERENCES users(id)
    );

-- Create customer_addresses table
CREATE TABLE IF NOT EXISTS customer_addresses (
                                                  id UUID PRIMARY KEY,
                                                  customer_id UUID NOT NULL,
                                                  label VARCHAR(100) NOT NULL,
    street TEXT NOT NULL,
    number VARCHAR(10) NOT NULL,
    complement VARCHAR(100),
    neighborhood VARCHAR(100) NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(50) NOT NULL,
    zip_code VARCHAR(20) NOT NULL,
    is_default BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    version BIGINT,
    FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE
    );

-- Create customer_favorites table
CREATE TABLE IF NOT EXISTS customer_favorites (
                                                  customer_id UUID NOT NULL,
                                                  product_id UUID NOT NULL,
                                                  PRIMARY KEY (customer_id, product_id),
    FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
    );

-- Create indexes
CREATE INDEX idx_customers_user_id ON customers(user_id);
CREATE INDEX idx_customer_addresses_customer_id ON customer_addresses(customer_id);
CREATE INDEX idx_customer_favorites_customer_id ON customer_favorites(customer_id);