CREATE DATABASE IF NOT EXISTS flash_sales DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE flash_sales;

CREATE TABLE IF NOT EXISTS campaigns (
    id                  VARCHAR(36)  NOT NULL,
    name                VARCHAR(255),
    description         TEXT,
    started_at          TIMESTAMP    NOT NULL,
    ended_at            TIMESTAMP    NOT NULL,
    created_by          VARCHAR(36),
    created_at          TIMESTAMP    NOT NULL,
    last_modified_by    VARCHAR(36),
    last_modified_at    TIMESTAMP    NOT NULL,
    is_deleted          BOOLEAN      NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS campaign_items (
    id                  VARCHAR(36)  NOT NULL,
    campaign_id       VARCHAR(36),
    product_id          VARCHAR(36),
    variant_id          VARCHAR(36),
    price         DECIMAL(19,2),
    stock               BIGINT       NOT NULL DEFAULT 0,
    sold_quantity       BIGINT       NOT NULL DEFAULT 0,
    created_by          VARCHAR(36),
    created_at          TIMESTAMP    NOT NULL,
    last_modified_by    VARCHAR(36),
    last_modified_at    TIMESTAMP    NOT NULL,
    is_deleted          BOOLEAN      NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id),
    UNIQUE KEY uk_flash_sale_items_variant (campaign_id, variant_id),
    CONSTRAINT fk_flash_sale_items_campaign_id FOREIGN KEY (campaign_id) REFERENCES campaigns (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
