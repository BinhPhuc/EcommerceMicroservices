CREATE DATABASE IF NOT EXISTS flash_sales DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE flash_sales;

CREATE TABLE IF NOT EXISTS flash_sales (
    id                  VARCHAR(36)  NOT NULL,
    name                VARCHAR(255),
    description         TEXT,
    status              VARCHAR(255),
    started_at          TIMESTAMP    NOT NULL,
    ended_at            TIMESTAMP    NOT NULL,
    created_by          VARCHAR(36),
    created_at          TIMESTAMP    NOT NULL,
    last_modified_by    VARCHAR(36),
    last_modified_at    TIMESTAMP    NOT NULL,
    is_deleted          BOOLEAN      NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS flash_sale_items (
    id                  VARCHAR(36)  NOT NULL,
    flash_sale_id       VARCHAR(36),
    product_id          VARCHAR(36),                 -- -> product_service.products.id
    variant_id          VARCHAR(36),                 -- -> product_service.product_variants.id
    flash_price         DECIMAL(19,2),
    stock               BIGINT       NOT NULL DEFAULT 0,
    sold_quantity       BIGINT       NOT NULL DEFAULT 0,
    purchase_limit      INT          NOT NULL DEFAULT 1,
    created_by          VARCHAR(36),
    created_at          TIMESTAMP    NOT NULL,
    last_modified_by    VARCHAR(36),
    last_modified_at    TIMESTAMP    NOT NULL,
    is_deleted          BOOLEAN      NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id),
    UNIQUE KEY uk_flash_sale_items_variant (flash_sale_id, variant_id),
    CONSTRAINT fk_flash_sale_items_flash_sale_id FOREIGN KEY (flash_sale_id) REFERENCES flash_sales (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
