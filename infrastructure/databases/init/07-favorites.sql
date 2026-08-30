CREATE DATABASE IF NOT EXISTS favourites DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE favourites;

CREATE TABLE IF NOT EXISTS favourites (
    id                  VARCHAR(36)  NOT NULL,
    user_id             VARCHAR(36),
    product_id          VARCHAR(36),
    created_by          VARCHAR(36),
    created_at          TIMESTAMP    NOT NULL,
    last_modified_by    VARCHAR(36),
    last_modified_at    TIMESTAMP    NOT NULL,
    is_deleted          BOOLEAN      NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id),
    UNIQUE KEY uk_favourites_user_product (user_id, product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

