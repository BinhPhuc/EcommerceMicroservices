CREATE DATABASE IF NOT EXISTS users DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE users;

CREATE TABLE IF NOT EXISTS users (
    id                  VARCHAR(36)  NOT NULL,
    name                VARCHAR(255),
    email               VARCHAR(255),
    phone_number        VARCHAR(255),
    birthday            TIMESTAMP    NULL,
    keycloak_user_id    VARCHAR(36),
    created_by          VARCHAR(36),
    created_at          TIMESTAMP    NOT NULL,
    last_modified_by    VARCHAR(36),
    last_modified_at    TIMESTAMP    NOT NULL,
    is_deleted          BOOLEAN      NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id),
    UNIQUE KEY uk_users_email (email),
    UNIQUE KEY uk_users_keycloak_user_id (keycloak_user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS sellers (
    id                  VARCHAR(36)  NOT NULL,
    store_name          VARCHAR(255),
    user_id             VARCHAR(36),
    logo_url            VARCHAR(255),
    description         TEXT,
    business_address    VARCHAR(255),
    status              VARCHAR(255),
    created_by          VARCHAR(36),
    created_at          TIMESTAMP    NOT NULL,
    last_modified_by    VARCHAR(36),
    last_modified_at    TIMESTAMP    NOT NULL,
    is_deleted          BOOLEAN      NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id),
    CONSTRAINT fk_sellers_user_id FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS user_addresses (
    id                  VARCHAR(36)  NOT NULL,
    user_id             VARCHAR(36),
    recipient_name      VARCHAR(255),
    phone_number        VARCHAR(255),
    province            VARCHAR(255),
    ward                VARCHAR(255),
    street_address      VARCHAR(255),
    is_default          BOOLEAN      NOT NULL DEFAULT FALSE,
    created_by          VARCHAR(36),
    created_at          TIMESTAMP    NOT NULL,
    last_modified_by    VARCHAR(36),
    last_modified_at    TIMESTAMP    NOT NULL,
    is_deleted          BOOLEAN      NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id),
    CONSTRAINT fk_user_addresses_user_id FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS seller_social_links (
    id                  VARCHAR(36)  NOT NULL,
    seller_id           VARCHAR(36),
    platform            VARCHAR(255),
    url                 VARCHAR(255),
    created_by          VARCHAR(36),
    created_at          TIMESTAMP    NOT NULL,
    last_modified_by    VARCHAR(36),
    last_modified_at    TIMESTAMP    NOT NULL,
    is_deleted          BOOLEAN      NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id),
    CONSTRAINT fk_seller_social_links_seller_id FOREIGN KEY (seller_id) REFERENCES sellers (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS user_memberships (
    id                  VARCHAR(36)  NOT NULL,
    user_id             VARCHAR(36),
    tier                VARCHAR(255),
    started_at          TIMESTAMP    NULL,
    expires_at          TIMESTAMP    NULL,
    created_by          VARCHAR(36),
    created_at          TIMESTAMP    NOT NULL,
    last_modified_by    VARCHAR(36),
    last_modified_at    TIMESTAMP    NOT NULL,
    is_deleted          BOOLEAN      NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id),
    CONSTRAINT fk_user_memberships_user_id FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
