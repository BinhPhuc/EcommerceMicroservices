CREATE DATABASE IF NOT EXISTS orders DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE orders;

CREATE TABLE IF NOT EXISTS orders (
    id                  VARCHAR(36)  NOT NULL,
    idempotency_key     VARCHAR(36),
    user_id             VARCHAR(36),
    total_amount        DECIMAL(19,2),
    status              VARCHAR(255),
    payment_method      VARCHAR(255),
    created_by          VARCHAR(36),
    created_at          TIMESTAMP    NOT NULL,
    last_modified_by    VARCHAR(36),
    last_modified_at    TIMESTAMP    NOT NULL,
    is_deleted          BOOLEAN      NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id),
    UNIQUE KEY uk_orders_idempotency_key (idempotency_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS order_snapshots (
    id                  VARCHAR(36)  NOT NULL,
    order_id            VARCHAR(36),
    shipping_address    VARCHAR(255),
    created_by          VARCHAR(36),
    created_at          TIMESTAMP    NOT NULL,
    last_modified_by    VARCHAR(36),
    last_modified_at    TIMESTAMP    NOT NULL,
    is_deleted          BOOLEAN      NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id),
    UNIQUE KEY uk_order_snapshots_order_id (order_id),
    CONSTRAINT fk_order_snapshots_order_id FOREIGN KEY (order_id) REFERENCES orders (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS seller_orders (
    id                  VARCHAR(36)  NOT NULL,
    order_id            VARCHAR(36),
    seller_id           VARCHAR(36),
    status              VARCHAR(255),
    shipping_fee        DECIMAL(19,2),
    created_by          VARCHAR(36),
    created_at          TIMESTAMP    NOT NULL,
    last_modified_by    VARCHAR(36),
    last_modified_at    TIMESTAMP    NOT NULL,
    is_deleted          BOOLEAN      NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id),
    CONSTRAINT fk_seller_orders_order_id FOREIGN KEY (order_id) REFERENCES orders (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS order_items (
    id                  VARCHAR(36)  NOT NULL,
    seller_order_id     VARCHAR(36),
    product_id          VARCHAR(36),
    variant_id          VARCHAR(36),
    quantity            INT          NOT NULL DEFAULT 1,
    created_by          VARCHAR(36),
    created_at          TIMESTAMP    NOT NULL,
    last_modified_by    VARCHAR(36),
    last_modified_at    TIMESTAMP    NOT NULL,
    is_deleted          BOOLEAN      NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id),
    CONSTRAINT fk_order_items_seller_order_id FOREIGN KEY (seller_order_id) REFERENCES seller_orders (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS order_item_snapshots (
    id                  VARCHAR(36)  NOT NULL,
    order_item_id       VARCHAR(36),
    product_name        VARCHAR(255),
    variant_attributes  JSON,
    price               DECIMAL(19,2),
    created_by          VARCHAR(36),
    created_at          TIMESTAMP    NOT NULL,
    last_modified_by    VARCHAR(36),
    last_modified_at    TIMESTAMP    NOT NULL,
    is_deleted          BOOLEAN      NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id),
    UNIQUE KEY uk_order_item_snapshots_order_item_id (order_item_id),  -- 1 order_item : 1 snapshot
    CONSTRAINT fk_order_item_snapshots_order_item_id FOREIGN KEY (order_item_id) REFERENCES order_items (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS order_outboxes (
    id                  VARCHAR(36)  NOT NULL,
    order_id            VARCHAR(36),
    processed           BOOLEAN      NOT NULL DEFAULT FALSE,
    created_by          VARCHAR(36),
    created_at          TIMESTAMP    NOT NULL,
    last_modified_by    VARCHAR(36),
    last_modified_at    TIMESTAMP    NOT NULL,
    is_deleted          BOOLEAN      NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id),
    CONSTRAINT fk_order_outboxes_order_id FOREIGN KEY (order_id) REFERENCES orders (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
