-- ============================================================
-- V3 - Catálogo
-- PostgreSQL
-- ============================================================

-- ============================================================
-- CATEGORIES
-- ============================================================

CREATE TABLE categories (
                            id UUID PRIMARY KEY,

                            name VARCHAR(100) NOT NULL,
                            slug VARCHAR(150) NOT NULL UNIQUE,

                            description TEXT,
                            image_url VARCHAR(500),

                            parent_id UUID,

                            active BOOLEAN NOT NULL DEFAULT TRUE,
                            display_order INTEGER NOT NULL DEFAULT 0,

                            created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            updated_at TIMESTAMP,

                            created_by VARCHAR(100),
                            updated_by VARCHAR(100),

                            version BIGINT,

                            CONSTRAINT fk_categories_parent
                                FOREIGN KEY (parent_id)
                                    REFERENCES categories(id)
);

-- ============================================================
-- BRANDS
-- ============================================================

CREATE TABLE brands (
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

-- ============================================================
-- PRODUCTS
-- ============================================================

CREATE TABLE products (
                          id UUID PRIMARY KEY,

                          name VARCHAR(200) NOT NULL,
                          slug VARCHAR(250) NOT NULL UNIQUE,

                          sku VARCHAR(50) NOT NULL UNIQUE,
                          ean VARCHAR(13),

                          description TEXT NOT NULL,
                          short_description VARCHAR(500),

                          price NUMERIC(10, 2) NOT NULL,

                          promotional_price NUMERIC(10, 2),

                          promotion_start TIMESTAMP,
                          promotion_end TIMESTAMP,

                          weight NUMERIC(10, 2) NOT NULL DEFAULT 0,
                          width NUMERIC(10, 2) NOT NULL DEFAULT 0,
                          height NUMERIC(10, 2) NOT NULL DEFAULT 0,
                          depth NUMERIC(10, 2) NOT NULL DEFAULT 0,

                          category_id UUID NOT NULL,
                          brand_id UUID,

                          specifications JSONB NOT NULL DEFAULT '{}'::jsonb,

                          active BOOLEAN NOT NULL DEFAULT TRUE,
                          featured BOOLEAN NOT NULL DEFAULT FALSE,

                          view_count BIGINT NOT NULL DEFAULT 0,

                          rating_average NUMERIC(3, 2) NOT NULL DEFAULT 0,
                          rating_count INTEGER NOT NULL DEFAULT 0,

                          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP,

                          created_by VARCHAR(100),
                          updated_by VARCHAR(100),

                          version BIGINT,

                          CONSTRAINT fk_products_category
                              FOREIGN KEY (category_id)
                                  REFERENCES categories(id),

                          CONSTRAINT fk_products_brand
                              FOREIGN KEY (brand_id)
                                  REFERENCES brands(id),

                          CONSTRAINT chk_products_price
                              CHECK (price >= 0),

                          CONSTRAINT chk_products_promotional_price
                              CHECK (
                                  promotional_price IS NULL
                                      OR promotional_price >= 0
                                  ),

                          CONSTRAINT chk_products_promotional_price_limit
                              CHECK (
                                  promotional_price IS NULL
                                      OR promotional_price <= price
                                  ),

                          CONSTRAINT chk_products_rating_average
                              CHECK (
                                  rating_average >= 0
                                      AND rating_average <= 5
                                  ),

                          CONSTRAINT chk_products_rating_count
                              CHECK (rating_count >= 0)
);

-- ============================================================
-- PRODUCT IMAGES
-- ============================================================

CREATE TABLE product_images (
                                id UUID PRIMARY KEY,

                                product_id UUID NOT NULL,

                                image_url VARCHAR(500) NOT NULL,
                                thumbnail_url VARCHAR(500),

                                alt_text VARCHAR(200),

                                display_order INTEGER NOT NULL DEFAULT 0,
                                is_primary BOOLEAN NOT NULL DEFAULT FALSE,

                                created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                updated_at TIMESTAMP,

                                version BIGINT,

                                CONSTRAINT fk_product_images_product
                                    FOREIGN KEY (product_id)
                                        REFERENCES products(id)
                                        ON DELETE CASCADE
);

-- ============================================================
-- CUSTOMER FAVORITES
--
-- Esta tabela fica nesta migration porque products passa a
-- existir somente a partir da V3.
-- ============================================================

CREATE TABLE customer_favorites (
                                    customer_id UUID NOT NULL,
                                    product_id UUID NOT NULL,

                                    PRIMARY KEY (
                                                 customer_id,
                                                 product_id
                                        ),

                                    CONSTRAINT fk_customer_favorites_customer
                                        FOREIGN KEY (customer_id)
                                            REFERENCES customers(id)
                                            ON DELETE CASCADE,

                                    CONSTRAINT fk_customer_favorites_product
                                        FOREIGN KEY (product_id)
                                            REFERENCES products(id)
                                            ON DELETE CASCADE
);

-- ============================================================
-- INDEXES
-- ============================================================

CREATE INDEX idx_categories_parent_id
    ON categories(parent_id);

CREATE INDEX idx_categories_active
    ON categories(active);

CREATE INDEX idx_brands_active
    ON brands(active);

CREATE INDEX idx_products_category_id
    ON products(category_id);

CREATE INDEX idx_products_brand_id
    ON products(brand_id);

CREATE INDEX idx_products_ean
    ON products(ean);

CREATE INDEX idx_products_active
    ON products(active);

CREATE INDEX idx_products_featured
    ON products(featured);

CREATE INDEX idx_products_price
    ON products(price);

CREATE INDEX idx_products_rating
    ON products(rating_average);

CREATE INDEX idx_product_images_product_id
    ON product_images(product_id);

CREATE INDEX idx_customer_favorites_customer_id
    ON customer_favorites(customer_id);

CREATE INDEX idx_customer_favorites_product_id
    ON customer_favorites(product_id);

-- ============================================================
-- INITIAL CATEGORIES
-- ============================================================

INSERT INTO categories (
    id,
    name,
    slug,
    description,
    active,
    display_order,
    created_at
)
VALUES
    (
        gen_random_uuid(),
        'Eletrodomésticos',
        'eletrodomesticos',
        'Linha completa de eletrodomésticos',
        TRUE,
        1,
        CURRENT_TIMESTAMP
    ),
    (
        gen_random_uuid(),
        'Eletrônicos',
        'eletronicos',
        'Eletrônicos e tecnologia',
        TRUE,
        2,
        CURRENT_TIMESTAMP
    ),
    (
        gen_random_uuid(),
        'Eletroportáteis',
        'eletroportateis',
        'Pequenos eletrodomésticos',
        TRUE,
        3,
        CURRENT_TIMESTAMP
    );

-- ============================================================
-- INITIAL BRANDS
-- ============================================================

INSERT INTO brands (
    id,
    name,
    slug,
    description,
    active,
    created_at
)
VALUES
    (
        gen_random_uuid(),
        'Samsung',
        'samsung',
        'Tecnologia e inovação',
        TRUE,
        CURRENT_TIMESTAMP
    ),
    (
        gen_random_uuid(),
        'LG',
        'lg',
        'Life is Good',
        TRUE,
        CURRENT_TIMESTAMP
    ),
    (
        gen_random_uuid(),
        'Electrolux',
        'electrolux',
        'Eletrodomésticos premium',
        TRUE,
        CURRENT_TIMESTAMP
    ),
    (
        gen_random_uuid(),
        'Brastemp',
        'brastemp',
        'Eletrodomésticos nacionais',
        TRUE,
        CURRENT_TIMESTAMP
    ),
    (
        gen_random_uuid(),
        'Philips',
        'philips',
        'Qualidade e tecnologia',
        TRUE,
        CURRENT_TIMESTAMP
    );