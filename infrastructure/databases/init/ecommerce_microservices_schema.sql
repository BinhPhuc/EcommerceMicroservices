-- =============================================================================
-- Ecommerce Microservices - DDL (database/schema per service)
-- Nguồn: ERD.drawio.xml
-- Dialect: MySQL 8.x / MariaDB (InnoDB, utf8mb4)
--
-- Quy ước chung:
--   * Mọi bảng kế thừa base_entity:
--       id, created_by, created_at, last_modified_at, last_modified_by, is_deleted
--     (base_entity là @MappedSuperclass ở tầng code -> KHÔNG tạo bảng riêng)
--   * id / mọi khoá ngoại: VARCHAR(36)  (độ dài UUID)
--   * thuộc tính chuỗi khác:  VARCHAR(255)
--   * thuộc tính ngày giờ:    TIMESTAMP
--   * FOREIGN KEY chỉ đặt TRONG CÙNG một schema. Các id trỏ sang service khác
--     (seller_id, user_id, product_id, variant_id...) chỉ là "soft reference":
--     lưu VARCHAR(36) + INDEX, không có FK vật lý -> giữ tính độc lập của service.
-- =============================================================================

SET NAMES utf8mb4;


-- =============================================================================
-- 1. USER_SERVICE
--    users, sellers, user_addresses, seller_social_links, user_memberships
-- =============================================================================
CREATE SCHEMA IF NOT EXISTS user_service DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE user_service;

CREATE TABLE IF NOT EXISTS users (
    id                  VARCHAR(36)  NOT NULL,
    name                VARCHAR(255),
    email               VARCHAR(255),
    phone_number        VARCHAR(255),
    birthday            TIMESTAMP    NULL,
    keycloak_user_id    VARCHAR(36),
    -- base_entity
    created_by          VARCHAR(255),
    created_at          TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_by    VARCHAR(255),
    last_modified_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted          BOOLEAN      NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id),
    UNIQUE KEY uk_users_email (email),
    UNIQUE KEY uk_users_keycloak_user_id (keycloak_user_id),
    INDEX idx_users_phone_number (phone_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS sellers (
    id                  VARCHAR(36)  NOT NULL,
    store_name          VARCHAR(255),
    user_id             VARCHAR(36),
    logo_url            VARCHAR(255),
    description         VARCHAR(255),
    business_address    VARCHAR(255),
    status              VARCHAR(255),
    -- base_entity
    created_by          VARCHAR(255),
    created_at          TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_by    VARCHAR(255),
    last_modified_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted          BOOLEAN      NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id),
    INDEX idx_sellers_user_id (user_id),
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
    -- base_entity
    created_by          VARCHAR(255),
    created_at          TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_by    VARCHAR(255),
    last_modified_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted          BOOLEAN      NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id),
    INDEX idx_user_addresses_user_id (user_id),
    CONSTRAINT fk_user_addresses_user_id FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS seller_social_links (
    id                  VARCHAR(36)  NOT NULL,
    seller_id           VARCHAR(36),
    platform            VARCHAR(255),
    url                 VARCHAR(255),
    -- base_entity
    created_by          VARCHAR(255),
    created_at          TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_by    VARCHAR(255),
    last_modified_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted          BOOLEAN      NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id),
    INDEX idx_seller_social_links_seller_id (seller_id),
    CONSTRAINT fk_seller_social_links_seller_id FOREIGN KEY (seller_id) REFERENCES sellers (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS user_memberships (
    id                  VARCHAR(36)  NOT NULL,
    user_id             VARCHAR(36),
    tier                VARCHAR(255),
    started_at          TIMESTAMP    NULL,
    expires_at          TIMESTAMP    NULL,
    -- base_entity
    created_by          VARCHAR(255),
    created_at          TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_by    VARCHAR(255),
    last_modified_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted          BOOLEAN      NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id),
    INDEX idx_user_memberships_user_id (user_id),
    CONSTRAINT fk_user_memberships_user_id FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- =============================================================================
-- 2. PRODUCT_SERVICE
--    categories, products, product_variants, product_images, product_statistics,
--    reviews, collections, collection_products
--    Soft reference: products.seller_id, collections.seller_id, reviews.user_id
--                    -> user_service.sellers / user_service.users
-- =============================================================================
CREATE SCHEMA IF NOT EXISTS product_service DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE product_service;

CREATE TABLE IF NOT EXISTS categories (
    id                  VARCHAR(36)  NOT NULL,
    name                VARCHAR(255),
    parent_id           VARCHAR(36),                 -- self reference (cây danh mục)
    -- base_entity
    created_by          VARCHAR(255),
    created_at          TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_by    VARCHAR(255),
    last_modified_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted          BOOLEAN      NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id),
    INDEX idx_categories_parent_id (parent_id),
    CONSTRAINT fk_categories_parent_id FOREIGN KEY (parent_id) REFERENCES categories (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS products (
    id                  VARCHAR(36)  NOT NULL,
    name                VARCHAR(255),
    description         VARCHAR(255),
    category_id         VARCHAR(36),
    seller_id           VARCHAR(36),                 -- -> user_service.sellers.id
    status              VARCHAR(255),
    units_sold          BIGINT       NOT NULL DEFAULT 0,
    -- base_entity
    created_by          VARCHAR(255),
    created_at          TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_by    VARCHAR(255),
    last_modified_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted          BOOLEAN      NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id),
    INDEX idx_products_category_id (category_id),
    INDEX idx_products_seller_id (seller_id),
    INDEX idx_products_status (status),
    CONSTRAINT fk_products_category_id FOREIGN KEY (category_id) REFERENCES categories (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS product_variants (
    id                  VARCHAR(36)  NOT NULL,
    product_id          VARCHAR(36),
    sku                 VARCHAR(255),
    attributes          VARCHAR(255),                -- JSON dạng chuỗi, vd {"color":"red","size":"M"}
    price               DECIMAL(19,2),
    -- base_entity
    created_by          VARCHAR(255),
    created_at          TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_by    VARCHAR(255),
    last_modified_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted          BOOLEAN      NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id),
    UNIQUE KEY uk_product_variants_sku (sku),
    INDEX idx_product_variants_product_id (product_id),
    CONSTRAINT fk_product_variants_product_id FOREIGN KEY (product_id) REFERENCES products (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS product_images (
    id                  VARCHAR(36)  NOT NULL,
    product_id          VARCHAR(36),
    url                 VARCHAR(255),
    is_thumbnail        BOOLEAN      NOT NULL DEFAULT FALSE,
    display_order       INT          NOT NULL DEFAULT 0,
    -- base_entity
    created_by          VARCHAR(255),
    created_at          TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_by    VARCHAR(255),
    last_modified_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted          BOOLEAN      NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id),
    INDEX idx_product_images_product_id (product_id),
    CONSTRAINT fk_product_images_product_id FOREIGN KEY (product_id) REFERENCES products (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS product_statistics (
    id                  VARCHAR(36)  NOT NULL,
    product_id          VARCHAR(36),
    average_rating      DECIMAL(3,2) NOT NULL DEFAULT 0.00,
    review_count        BIGINT       NOT NULL DEFAULT 0,
    -- base_entity
    created_by          VARCHAR(255),
    created_at          TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_by    VARCHAR(255),
    last_modified_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted          BOOLEAN      NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id),
    UNIQUE KEY uk_product_statistics_product_id (product_id),   -- 1 product : 1 statistics
    CONSTRAINT fk_product_statistics_product_id FOREIGN KEY (product_id) REFERENCES products (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS reviews (
    id                  VARCHAR(36)  NOT NULL,
    product_id          VARCHAR(36),
    user_id             VARCHAR(36),                 -- -> user_service.users.id
    subject             VARCHAR(255),
    body                VARCHAR(255),
    rating              DECIMAL(2,1),                -- 0.0 .. 5.0
    like_count          BIGINT       NOT NULL DEFAULT 0,
    -- base_entity
    created_by          VARCHAR(255),
    created_at          TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_by    VARCHAR(255),
    last_modified_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted          BOOLEAN      NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id),
    INDEX idx_reviews_product_id (product_id),
    INDEX idx_reviews_user_id (user_id),
    CONSTRAINT fk_reviews_product_id FOREIGN KEY (product_id) REFERENCES products (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS collections (
    id                  VARCHAR(36)  NOT NULL,
    name                VARCHAR(255),
    seller_id           VARCHAR(36),                 -- -> user_service.sellers.id
    -- base_entity
    created_by          VARCHAR(255),
    created_at          TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_by    VARCHAR(255),
    last_modified_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted          BOOLEAN      NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id),
    INDEX idx_collections_seller_id (seller_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS collection_products (
    id                  VARCHAR(36)  NOT NULL,
    collection_id       VARCHAR(36),
    product_id          VARCHAR(36),
    -- base_entity
    created_by          VARCHAR(255),
    created_at          TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_by    VARCHAR(255),
    last_modified_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted          BOOLEAN      NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id),
    UNIQUE KEY uk_collection_products (collection_id, product_id),
    INDEX idx_collection_products_product_id (product_id),
    CONSTRAINT fk_collection_products_collection_id FOREIGN KEY (collection_id) REFERENCES collections (id),
    CONSTRAINT fk_collection_products_product_id    FOREIGN KEY (product_id)    REFERENCES products (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- =============================================================================
-- 3. ORDER_SERVICE
--    orders, order_snapshots, seller_orders, order_items, order_item_snapshots
--    Soft reference: orders.user_id, seller_orders.seller_id,
--                    order_items.product_id / variant_id
-- =============================================================================
CREATE SCHEMA IF NOT EXISTS order_service DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE order_service;

CREATE TABLE IF NOT EXISTS orders (
    id                  VARCHAR(36)  NOT NULL,
    user_id             VARCHAR(36),                 -- -> user_service.users.id
    total_amount        DECIMAL(19,2),
    status              VARCHAR(255),
    payment_method      VARCHAR(255),
    -- base_entity
    created_by          VARCHAR(255),
    created_at          TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_by    VARCHAR(255),
    last_modified_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted          BOOLEAN      NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id),
    INDEX idx_orders_user_id (user_id),
    INDEX idx_orders_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS order_snapshots (
    id                  VARCHAR(36)  NOT NULL,
    order_id            VARCHAR(36),
    shipping_address    VARCHAR(255),
    -- base_entity
    created_by          VARCHAR(255),
    created_at          TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_by    VARCHAR(255),
    last_modified_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted          BOOLEAN      NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id),
    UNIQUE KEY uk_order_snapshots_order_id (order_id),          -- 1 order : 1 snapshot
    CONSTRAINT fk_order_snapshots_order_id FOREIGN KEY (order_id) REFERENCES orders (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS seller_orders (
    id                  VARCHAR(36)  NOT NULL,
    order_id            VARCHAR(36),
    seller_id           VARCHAR(36),                 -- -> user_service.sellers.id
    status              VARCHAR(255),
    shipping_fee        DECIMAL(19,2),
    -- base_entity
    created_by          VARCHAR(255),
    created_at          TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_by    VARCHAR(255),
    last_modified_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted          BOOLEAN      NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id),
    INDEX idx_seller_orders_order_id (order_id),
    INDEX idx_seller_orders_seller_id (seller_id),
    CONSTRAINT fk_seller_orders_order_id FOREIGN KEY (order_id) REFERENCES orders (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS order_items (
    id                  VARCHAR(36)  NOT NULL,
    seller_order_id     VARCHAR(36),
    product_id          VARCHAR(36),                 -- -> product_service.products.id
    variant_id          VARCHAR(36),                 -- -> product_service.product_variants.id
    quantity            INT          NOT NULL DEFAULT 1,
    -- base_entity
    created_by          VARCHAR(255),
    created_at          TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_by    VARCHAR(255),
    last_modified_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted          BOOLEAN      NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id),
    INDEX idx_order_items_seller_order_id (seller_order_id),
    INDEX idx_order_items_product_id (product_id),
    INDEX idx_order_items_variant_id (variant_id),
    CONSTRAINT fk_order_items_seller_order_id FOREIGN KEY (seller_order_id) REFERENCES seller_orders (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS order_item_snapshots (
    id                  VARCHAR(36)  NOT NULL,
    order_item_id       VARCHAR(36),
    product_name        VARCHAR(255),
    variant_attributes  VARCHAR(255),
    price               DECIMAL(19,2),
    -- base_entity
    created_by          VARCHAR(255),
    created_at          TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_by    VARCHAR(255),
    last_modified_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted          BOOLEAN      NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id),
    UNIQUE KEY uk_order_item_snapshots_order_item_id (order_item_id),  -- 1 order_item : 1 snapshot
    CONSTRAINT fk_order_item_snapshots_order_item_id FOREIGN KEY (order_item_id) REFERENCES order_items (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- =============================================================================
-- 4. INVENTORY_SERVICE
-- =============================================================================
CREATE SCHEMA IF NOT EXISTS inventory_service DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE inventory_service;

CREATE TABLE IF NOT EXISTS inventories (
    id                  VARCHAR(36)  NOT NULL,
    variant_id          VARCHAR(36),                 -- -> product_service.product_variants.id
    stock               BIGINT       NOT NULL DEFAULT 0,
    -- base_entity
    created_by          VARCHAR(255),
    created_at          TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_by    VARCHAR(255),
    last_modified_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted          BOOLEAN      NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id),
    UNIQUE KEY uk_inventories_variant_id (variant_id)            -- 1 variant : 1 dòng tồn kho
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- =============================================================================
-- 5. MEDIA_SERVICE
-- =============================================================================
CREATE SCHEMA IF NOT EXISTS media_service DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE media_service;

CREATE TABLE IF NOT EXISTS medias (
    id                  VARCHAR(36)  NOT NULL,
    user_id             VARCHAR(36),                 -- -> user_service.users.id (người upload)
    original_name       VARCHAR(255),
    media_type          VARCHAR(255),
    cdn_url             VARCHAR(255),
    -- base_entity
    created_by          VARCHAR(255),
    created_at          TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_by    VARCHAR(255),
    last_modified_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted          BOOLEAN      NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id),
    INDEX idx_medias_user_id (user_id),
    INDEX idx_medias_media_type (media_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- =============================================================================
-- 6. FAVOURITE_SERVICE
-- =============================================================================
CREATE SCHEMA IF NOT EXISTS favourite_service DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE favourite_service;

CREATE TABLE IF NOT EXISTS favourites (
    id                  VARCHAR(36)  NOT NULL,
    user_id             VARCHAR(36),                 -- -> user_service.users.id
    product_id          VARCHAR(36),                 -- -> product_service.products.id
    -- base_entity
    created_by          VARCHAR(255),
    created_at          TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_by    VARCHAR(255),
    last_modified_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted          BOOLEAN      NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id),
    UNIQUE KEY uk_favourites_user_product (user_id, product_id),
    INDEX idx_favourites_product_id (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- =============================================================================
-- 7. KEYCLOAK (schema riêng cho Identity Provider, Keycloak tự tạo bảng)
-- =============================================================================
CREATE SCHEMA IF NOT EXISTS keycloak DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
