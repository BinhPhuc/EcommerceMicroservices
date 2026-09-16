package com.binhphuc.flash_sale_service.constant;

import java.time.Duration;

public final class OrderOutboxConstant {
    private OrderOutboxConstant() {
    }

    public static final String OUTBOX_STREAM_KEY = "orderOutbox";
    public static final String OUTBOX_CONSUMER_GROUP = "flash-sale-service.v1";
    public static final String OUTBOX_PAYLOAD_FIELD = "payload";
    public static final String ORDER_REQUEST_CACHE_KEY_PREFIX = "orderRequest::";
    public static final long ORDER_REQUEST_TTL_SECONDS = Duration.ofDays(1).toSeconds();
    public static final long OUTBOX_MAX_LENGTH = 100_000L;
    public static final long MAX_DELIVERY_ATTEMPTS = 3L;
    public static final long RECOVERY_BATCH_SIZE = 100L;
    public static final Duration OUTBOX_POLL_TIMEOUT = Duration.ofSeconds(2);
    public static final Duration OUTBOX_SEND_TIMEOUT = Duration.ofSeconds(5);
    public static final Duration MIN_IDLE_TIME_BEFORE_RETRY = Duration.ofSeconds(30);
}
