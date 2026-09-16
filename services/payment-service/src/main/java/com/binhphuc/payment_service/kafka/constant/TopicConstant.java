package com.binhphuc.payment_service.kafka.constant;

public final class TopicConstant {
    private TopicConstant() {
    }

    public static final String PAYMENT_CHARGE_PAYMENT_TOPIC = "payment.charge-payment.v1";
    public static final String PAYMENT_FAILED_TOPIC = "payment.payment-failed.v1";
}
