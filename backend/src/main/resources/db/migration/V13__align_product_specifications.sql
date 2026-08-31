-- ============================================================
-- V13 - Alinha as especificações de produto ao mapeamento JPA.
--
-- A V3 criou products.specifications como JSONB.
-- A entidade Product utiliza @ElementCollection e espera:
--
-- product_specifications (
--     product_id,
--     spec_key,
--     spec_value
-- )
-- ============================================================

CREATE TABLE product_specifications (
                                        product_id UUID NOT NULL,
                                        spec_key VARCHAR(100) NOT NULL,
                                        spec_value TEXT,

                                        CONSTRAINT pk_product_specifications
                                            PRIMARY KEY (product_id, spec_key),

                                        CONSTRAINT fk_product_specifications_product
                                            FOREIGN KEY (product_id)
                                                REFERENCES products(id)
                                                ON DELETE CASCADE
);

CREATE INDEX idx_product_specifications_product_id
    ON product_specifications(product_id);

-- Migra eventuais especificações existentes no JSONB da V3
-- para o modelo chave/valor esperado pelo Hibernate.
INSERT INTO product_specifications (
    product_id,
    spec_key,
    spec_value
)
SELECT
    p.id,
    specification.key,
    specification.value
FROM products p
         CROSS JOIN LATERAL jsonb_each_text(
        COALESCE(p.specifications, '{}'::jsonb)
                            ) AS specification(key, value)
ON CONFLICT (product_id, spec_key) DO NOTHING;

-- A coluna antiga deixa de ser necessária após a migração.
ALTER TABLE products
    DROP COLUMN specifications;