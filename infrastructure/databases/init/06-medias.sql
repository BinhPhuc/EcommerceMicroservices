CREATE DATABASE IF NOT EXISTS medias DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE medias;

CREATE TABLE IF NOT EXISTS medias (
    id                  VARCHAR(36)  NOT NULL,
    user_id             VARCHAR(36),
    original_name       VARCHAR(255),
    media_type          VARCHAR(255),
    cdn_url             VARCHAR(255),
    created_by          VARCHAR(36),
    created_at          TIMESTAMP    NOT NULL,
    last_modified_by    VARCHAR(36),
    last_modified_at    TIMESTAMP    NOT NULL,
    is_deleted          BOOLEAN      NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


