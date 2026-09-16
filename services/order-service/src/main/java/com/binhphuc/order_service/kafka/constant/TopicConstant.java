package com.binhphuc.order_service.kafka.constant;

public final class TopicConstant {
    private TopicConstant() {
    }

    public static final String ORDER_CREATE_TOPIC = "order.create.v1";
    public static final String PRODUCT_LOCKED_TOPIC = "product.lock-stock.v1";
    public static final String FLASH_SALE_CREATE_ORDER_TOPIC = "flash-sale.create-order.v1";
    public static final String PAYMENT_CHARGE_PAYMENT_TOPIC = "payment.charge-payment.v1";
    public static final String PAYMENT_FAILED_TOPIC = "payment.payment-failed.v1";
}
