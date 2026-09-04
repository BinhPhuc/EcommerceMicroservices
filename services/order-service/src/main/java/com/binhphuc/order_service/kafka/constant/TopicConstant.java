package com.binhphuc.order_service.kafka.constant;

public final class TopicConstant {
    private TopicConstant() {
    }

    public static final String ORDER_CREATE_TOPIC = "order.create.v1";
    public static final String PRODUCT_LOCKED_TOPIC = "product.lock-stock.v1";
}
