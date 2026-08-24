-- Create reviews table
CREATE TABLE IF NOT EXISTS reviews (
                                       id UUID PRIMARY KEY,
                                       product_id UUID NOT NULL,
                                       user_id UUID NOT NULL,
                                       rating DECIMAL(3, 2) NOT NULL,
    title VARCHAR(100) NOT NULL,
    content TEXT NOT NULL,
    helpful_count INTEGER DEFAULT 0,
    unhelpful_count INTEGER DEFAULT 0,
    verified BOOLEAN DEFAULT FALSE,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT,
    FOREIGN KEY (product_id) REFERENCES products(id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT chk_rating CHECK (rating >= 1 AND rating <= 5)
    );

-- Create indexes
CREATE INDEX idx_reviews_product_id ON reviews(product_id);
CREATE INDEX idx_reviews_user_id ON reviews(user_id);
CREATE INDEX idx_reviews_rating ON reviews(rating);
CREATE INDEX idx_reviews_active ON reviews(active);
CREATE INDEX idx_reviews_created_at ON reviews(created_at DESC);