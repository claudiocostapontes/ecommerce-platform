-- Corrige valores NULL existentes nas colunas de controle otimista
-- utilizadas pelo Hibernate através de @Version.

UPDATE users
SET version = 0
WHERE version IS NULL;

UPDATE roles
SET version = 0
WHERE version IS NULL;

UPDATE customers
SET version = 0
WHERE version IS NULL;

UPDATE customer_addresses
SET version = 0
WHERE version IS NULL;

UPDATE categories
SET version = 0
WHERE version IS NULL;

UPDATE brands
SET version = 0
WHERE version IS NULL;

UPDATE products
SET version = 0
WHERE version IS NULL;

UPDATE product_images
SET version = 0
WHERE version IS NULL;

UPDATE inventory
SET version = 0
WHERE version IS NULL;

UPDATE inventory_movements
SET version = 0
WHERE version IS NULL;

UPDATE carts
SET version = 0
WHERE version IS NULL;

UPDATE cart_items
SET version = 0
WHERE version IS NULL;

UPDATE orders
SET version = 0
WHERE version IS NULL;

UPDATE order_items
SET version = 0
WHERE version IS NULL;

UPDATE order_status_history
SET version = 0
WHERE version IS NULL;

UPDATE payments
SET version = 0
WHERE version IS NULL;

UPDATE shipments
SET version = 0
WHERE version IS NULL;

UPDATE coupons
SET version = 0
WHERE version IS NULL;

UPDATE reviews
SET version = 0
WHERE version IS NULL;


-- Define valor padrão para novos registros criados diretamente via SQL.

ALTER TABLE users
    ALTER COLUMN version SET DEFAULT 0;

ALTER TABLE roles
    ALTER COLUMN version SET DEFAULT 0;

ALTER TABLE customers
    ALTER COLUMN version SET DEFAULT 0;

ALTER TABLE customer_addresses
    ALTER COLUMN version SET DEFAULT 0;

ALTER TABLE categories
    ALTER COLUMN version SET DEFAULT 0;

ALTER TABLE brands
    ALTER COLUMN version SET DEFAULT 0;

ALTER TABLE products
    ALTER COLUMN version SET DEFAULT 0;

ALTER TABLE product_images
    ALTER COLUMN version SET DEFAULT 0;

ALTER TABLE inventory
    ALTER COLUMN version SET DEFAULT 0;

ALTER TABLE inventory_movements
    ALTER COLUMN version SET DEFAULT 0;

ALTER TABLE carts
    ALTER COLUMN version SET DEFAULT 0;

ALTER TABLE cart_items
    ALTER COLUMN version SET DEFAULT 0;

ALTER TABLE orders
    ALTER COLUMN version SET DEFAULT 0;

ALTER TABLE order_items
    ALTER COLUMN version SET DEFAULT 0;

ALTER TABLE order_status_history
    ALTER COLUMN version SET DEFAULT 0;

ALTER TABLE payments
    ALTER COLUMN version SET DEFAULT 0;

ALTER TABLE shipments
    ALTER COLUMN version SET DEFAULT 0;

ALTER TABLE coupons
    ALTER COLUMN version SET DEFAULT 0;

ALTER TABLE reviews
    ALTER COLUMN version SET DEFAULT 0;


-- Após corrigir os registros existentes, impede versões NULL.
-- Isso mantém o schema compatível com entidades JPA que usam @Version.

ALTER TABLE users
    ALTER COLUMN version SET NOT NULL;

ALTER TABLE roles
    ALTER COLUMN version SET NOT NULL;

ALTER TABLE customers
    ALTER COLUMN version SET NOT NULL;

ALTER TABLE customer_addresses
    ALTER COLUMN version SET NOT NULL;

ALTER TABLE categories
    ALTER COLUMN version SET NOT NULL;

ALTER TABLE brands
    ALTER COLUMN version SET NOT NULL;

ALTER TABLE products
    ALTER COLUMN version SET NOT NULL;

ALTER TABLE product_images
    ALTER COLUMN version SET NOT NULL;

ALTER TABLE inventory
    ALTER COLUMN version SET NOT NULL;

ALTER TABLE inventory_movements
    ALTER COLUMN version SET NOT NULL;

ALTER TABLE carts
    ALTER COLUMN version SET NOT NULL;

ALTER TABLE cart_items
    ALTER COLUMN version SET NOT NULL;

ALTER TABLE orders
    ALTER COLUMN version SET NOT NULL;

ALTER TABLE order_items
    ALTER COLUMN version SET NOT NULL;

ALTER TABLE order_status_history
    ALTER COLUMN version SET NOT NULL;

ALTER TABLE payments
    ALTER COLUMN version SET NOT NULL;

ALTER TABLE shipments
    ALTER COLUMN version SET NOT NULL;

ALTER TABLE coupons
    ALTER COLUMN version SET NOT NULL;

ALTER TABLE reviews
    ALTER COLUMN version SET NOT NULL;