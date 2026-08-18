CREATE DATABASE IF NOT EXISTS products DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE products;

CREATE TABLE IF NOT EXISTS categories (
    id                  VARCHAR(36)  NOT NULL,
    name                VARCHAR(255),
    parent_id           VARCHAR(36),
    created_by          VARCHAR(36),
    created_at          TIMESTAMP    NOT NULL,
    last_modified_by    VARCHAR(36),
    last_modified_at    TIMESTAMP    NOT NULL,
    is_deleted          BOOLEAN      NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id),
    CONSTRAINT fk_categories_parent_id FOREIGN KEY (parent_id) REFERENCES categories (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS products (
    id                  VARCHAR(36)  NOT NULL,
    name                VARCHAR(255),
    description         TEXT,
    category_id         VARCHAR(36),
    seller_id           VARCHAR(36),
    status              VARCHAR(255),
    units_sold          BIGINT       NOT NULL DEFAULT 0,
    created_by          VARCHAR(36),
    created_at          TIMESTAMP    NOT NULL,
    last_modified_by    VARCHAR(36),
    last_modified_at    TIMESTAMP    NOT NULL,
    is_deleted          BOOLEAN      NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id),
    CONSTRAINT fk_products_category_id FOREIGN KEY (category_id) REFERENCES categories (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS product_variants (
    id                  VARCHAR(36)  NOT NULL,
    product_id          VARCHAR(36),
    sku                 VARCHAR(255),
    attributes          JSON,
    price               DECIMAL(19,2),
    created_by          VARCHAR(36),
    created_at          TIMESTAMP    NOT NULL,
    last_modified_by    VARCHAR(36),
    last_modified_at    TIMESTAMP    NOT NULL,
    is_deleted          BOOLEAN      NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id),
    UNIQUE KEY uk_product_variants_sku (sku),
    CONSTRAINT fk_product_variants_product_id FOREIGN KEY (product_id) REFERENCES products (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS product_images (
    id                  VARCHAR(36)  NOT NULL,
    product_id          VARCHAR(36),
    url                 VARCHAR(255),
    is_thumbnail        BOOLEAN      NOT NULL DEFAULT FALSE,
    display_order       INT          NOT NULL DEFAULT 0,
    created_by          VARCHAR(36),
    created_at          TIMESTAMP    NOT NULL,
    last_modified_by    VARCHAR(36),
    last_modified_at    TIMESTAMP    NOT NULL,
    is_deleted          BOOLEAN      NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id),
    CONSTRAINT fk_product_images_product_id FOREIGN KEY (product_id) REFERENCES products (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS product_statistics (
    id                  VARCHAR(36)  NOT NULL,
    product_id          VARCHAR(36),
    average_rating      DECIMAL(3,2) NOT NULL DEFAULT 0.00,
    review_count        BIGINT       NOT NULL DEFAULT 0,
    created_by          VARCHAR(36),
    created_at          TIMESTAMP    NOT NULL,
    last_modified_by    VARCHAR(36),
    last_modified_at    TIMESTAMP    NOT NULL,
    is_deleted          BOOLEAN      NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id),
    UNIQUE KEY uk_product_statistics_product_id (product_id),
    CONSTRAINT fk_product_statistics_product_id FOREIGN KEY (product_id) REFERENCES products (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS reviews (
    id                  VARCHAR(36)  NOT NULL,
    product_id          VARCHAR(36),
    user_id             VARCHAR(36),
    subject             VARCHAR(255),
    body                VARCHAR(255),
    rating              DECIMAL(2,1),
    like_count          BIGINT       NOT NULL DEFAULT 0,
    created_by          VARCHAR(36),
    created_at          TIMESTAMP    NOT NULL,
    last_modified_by    VARCHAR(36),
    last_modified_at    TIMESTAMP    NOT NULL,
    is_deleted          BOOLEAN      NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id),
    CONSTRAINT fk_reviews_product_id FOREIGN KEY (product_id) REFERENCES products (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS collections (
    id                  VARCHAR(36)  NOT NULL,
    name                VARCHAR(255),
    seller_id           VARCHAR(36),
    created_by          VARCHAR(36),
    created_at          TIMESTAMP    NOT NULL,
    last_modified_by    VARCHAR(36),
    last_modified_at    TIMESTAMP    NOT NULL,
    is_deleted          BOOLEAN      NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id),
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS collection_products (
    id                  VARCHAR(36)  NOT NULL,
    collection_id       VARCHAR(36),
    product_id          VARCHAR(36),
    created_by          VARCHAR(36),
    created_at          TIMESTAMP    NOT NULL,
    last_modified_by    VARCHAR(36),
    last_modified_at    TIMESTAMP    NOT NULL,
    is_deleted          BOOLEAN      NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id),
    UNIQUE KEY uk_collection_products (collection_id, product_id),
    CONSTRAINT fk_collection_products_collection_id FOREIGN KEY (collection_id) REFERENCES collections (id),
    CONSTRAINT fk_collection_products_product_id    FOREIGN KEY (product_id)    REFERENCES products (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
