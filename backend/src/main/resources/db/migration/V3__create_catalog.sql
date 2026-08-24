-- Create categories table
CREATE TABLE IF NOT EXISTS categories (
                                          id UUID PRIMARY KEY,
                                          name VARCHAR(100) NOT NULL,
    slug VARCHAR(150) NOT NULL UNIQUE,
    description TEXT,
    image_url VARCHAR(500),
    parent_id UUID,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    display_order INTEGER DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT,
    FOREIGN KEY (parent_id) REFERENCES categories(id)
    );

-- Create brands table
CREATE TABLE IF NOT EXISTS brands (
                                      id UUID PRIMARY KEY,
                                      name VARCHAR(100) NOT NULL UNIQUE,
    slug VARCHAR(150) NOT NULL UNIQUE,
    description TEXT,
    logo_url VARCHAR(500),
    website_url VARCHAR(300),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT
    );

-- Create products table
CREATE TABLE IF NOT EXISTS products (
                                        id UUID PRIMARY KEY,
                                        name VARCHAR(200) NOT NULL,
    slug VARCHAR(250) NOT NULL UNIQUE,
    sku VARCHAR(50) NOT NULL UNIQUE,
    ean VARCHAR(13),
    description TEXT NOT NULL,
    short_description VARCHAR(500),
    price DECIMAL(10, 2) NOT NULL,
    promotional_price DECIMAL(10, 2),
    promotion_start TIMESTAMP,
    promotion_end TIMESTAMP,
    weight DECIMAL(5, 2) DEFAULT 0,
    width DECIMAL(5, 2) DEFAULT 0,
    height DECIMAL(5, 2) DEFAULT 0,
    depth DECIMAL(5, 2) DEFAULT 0,
    category_id UUID NOT NULL,
    brand_id UUID,
    specifications JSONB,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    featured BOOLEAN DEFAULT FALSE,
    view_count BIGINT DEFAULT 0,
    rating_average DECIMAL(3, 2) DEFAULT 0,
    rating_count INTEGER DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT,
    FOREIGN KEY (category_id) REFERENCES categories(id),
    FOREIGN KEY (brand_id) REFERENCES brands(id)
    );

-- Create product_images table
CREATE TABLE IF NOT EXISTS product_images (
                                              id UUID PRIMARY KEY,
                                              product_id UUID NOT NULL,
                                              image_url VARCHAR(500) NOT NULL,
    thumbnail_url VARCHAR(500),
    alt_text VARCHAR(200),
    display_order INTEGER DEFAULT 0,
    is_primary BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    version BIGINT,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
    );

-- Create indexes
CREATE INDEX idx_categories_slug ON categories(slug);
CREATE INDEX idx_categories_parent_id ON categories(parent_id);
CREATE INDEX idx_categories_active ON categories(active);

CREATE INDEX idx_brands_slug ON brands(slug);
CREATE INDEX idx_brands_active ON brands(active);

CREATE INDEX idx_products_slug ON products(slug);
CREATE INDEX idx_products_sku ON products(sku);
CREATE INDEX idx_products_ean ON products(ean);
CREATE INDEX idx_products_category_id ON products(category_id);
CREATE INDEX idx_products_brand_id ON products(brand_id);
CREATE INDEX idx_products_active ON products(active);
CREATE INDEX idx_products_featured ON products(featured);
CREATE INDEX idx_products_price ON products(price);
CREATE INDEX idx_products_rating ON products(rating_average);

CREATE INDEX idx_product_images_product_id ON product_images(product_id);

-- Insert sample categories
INSERT INTO categories (id, name, slug, description, active, display_order, created_at) VALUES
                                                                                            (RANDOM_UUID(), 'Eletrodomésticos', 'eletrodomesticos', 'Linha completa de eletrodomésticos', TRUE, 1, CURRENT_TIMESTAMP),
                                                                                            (RANDOM_UUID(), 'Eletrônicos', 'eletronicos', 'Eletrônicos e tecnologia', TRUE, 2, CURRENT_TIMESTAMP),
                                                                                            (RANDOM_UUID(), 'Eletroportáteis', 'eletroportateis', 'Pequenos eletrodomésticos', TRUE, 3, CURRENT_TIMESTAMP);

-- Insert sample brands
INSERT INTO brands (id, name, slug, description, active, created_at) VALUES
                                                                         (RANDOM_UUID(), 'Samsung', 'samsung', 'Tecnologia e inovação', TRUE, CURRENT_TIMESTAMP),
                                                                         (RANDOM_UUID(), 'LG', 'lg', 'Life is Good', TRUE, CURRENT_TIMESTAMP),
                                                                         (RANDOM_UUID(), 'Electrolux', 'electrolux', 'Eletrodomésticos premium', TRUE, CURRENT_TIMESTAMP),
                                                                         (RANDOM_UUID(), 'Brastemp', 'brastemp', 'Eletrodomésticos nacionais', TRUE, CURRENT_TIMESTAMP),
                                                                         (RANDOM_UUID(), 'Philips', 'philips', 'Qualidade e tecnologia', TRUE, CURRENT_TIMESTAMP);