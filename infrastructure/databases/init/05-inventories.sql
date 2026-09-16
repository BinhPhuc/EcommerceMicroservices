CREATE DATABASE IF NOT EXISTS inventories DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE inventories;

CREATE TABLE IF NOT EXISTS inventories (
    id                  VARCHAR(36)  NOT NULL,
    variant_id          VARCHAR(36),
    stock               BIGINT       NOT NULL DEFAULT 0,
    created_by          VARCHAR(36),
    created_at          TIMESTAMP    NOT NULL,
    last_modified_by    VARCHAR(36),
    last_modified_at    TIMESTAMP    NOT NULL,
    is_deleted          BOOLEAN      NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id),
    UNIQUE KEY uk_inventories_variant_id (variant_id)            -- 1 variant : 1 dòng tồn kho
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
