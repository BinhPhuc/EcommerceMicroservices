package com.binhphuc.product_service.kafka.constant;

public final class TopicConstant {
    private TopicConstant() {
    }

    public static final String PRODUCT_LOCKED_TOPIC = "product.lock-stock.v1";
    public static final String ORDER_CREATED_TOPIC = "order.create.v1";
    public static final String PRE_WARM_ITEM_TOPIC = "flash-sale.pre-warm.v1";
}
