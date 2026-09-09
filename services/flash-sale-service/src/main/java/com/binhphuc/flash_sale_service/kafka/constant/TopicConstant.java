package com.binhphuc.flash_sale_service.kafka.constant;

public final class TopicConstant {
    private TopicConstant() {
    }

    public static final String PRE_WARM_ITEM_TOPIC = "flash-sale.pre-warm.v1";
    public static final String FLASH_SALE_CREATE_ORDER_TOPIC = "flash-sale.create-order.v1";
}
