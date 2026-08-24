-- Create inventory table
CREATE TABLE IF NOT EXISTS inventory (
                                         id UUID PRIMARY KEY,
                                         product_id UUID NOT NULL UNIQUE,
                                         quantity_available INTEGER NOT NULL DEFAULT 0,
                                         quantity_reserved INTEGER NOT NULL DEFAULT 0,
                                         minimum_stock INTEGER DEFAULT 0,
                                         location VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT,
    FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT chk_quantity_available CHECK (quantity_available >= 0),
    CONSTRAINT chk_quantity_reserved CHECK (quantity_reserved >= 0)
    );

-- Create inventory_movements table
CREATE TABLE IF NOT EXISTS inventory_movements (
                                                   id UUID PRIMARY KEY,
                                                   product_id UUID NOT NULL,
                                                   movement_type VARCHAR(50) NOT NULL,
    quantity INTEGER NOT NULL,
    quantity_before INTEGER NOT NULL,
    quantity_after INTEGER NOT NULL,
    reference_id UUID,
    reference_type VARCHAR(50),
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT,
    FOREIGN KEY (product_id) REFERENCES products(id)
    );

-- Create indexes
CREATE INDEX idx_inventory_product_id ON inventory(product_id);
CREATE INDEX idx_inventory_movements_product_id ON inventory_movements(product_id);
CREATE INDEX idx_inventory_movements_type ON inventory_movements(movement_type);
CREATE INDEX idx_inventory_movements_reference ON inventory_movements(reference_id);
CREATE INDEX idx_inventory_movements_created_at ON inventory_movements(created_at DESC);