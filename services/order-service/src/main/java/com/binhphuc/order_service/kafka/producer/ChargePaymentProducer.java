package com.binhphuc.order_service.kafka.producer;

import com.binhphuc.order_service.kafka.constant.TopicConstant;
import com.binhphuc.order_service.kafka.event.ChargePaymentEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class ChargePaymentProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public CompletableFuture<SendResult<String, Object>> chargePayment(ChargePaymentEvent chargePaymentEvent) {
        return kafkaTemplate.send(TopicConstant.PAYMENT_CHARGE_PAYMENT_TOPIC, chargePaymentEvent);
    }
}
