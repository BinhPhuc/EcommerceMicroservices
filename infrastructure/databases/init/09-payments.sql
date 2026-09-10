CREATE DATABASE IF NOT EXISTS payments DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE payments;

CREATE TABLE IF NOT EXISTS processed_events (
    id                  VARCHAR(36)  NOT NULL,
    idempotency_key     VARCHAR(36)  NOT NULL,
    created_by          VARCHAR(36),
    created_at          TIMESTAMP    NOT NULL,
    last_modified_by    VARCHAR(36),
    last_modified_at    TIMESTAMP    NOT NULL,
    is_deleted          BOOLEAN      NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id),
    UNIQUE KEY idx_processed_events_idempotency_key (idempotency_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
