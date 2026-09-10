package com.binhphuc.payment_service.kafka.producer;

import com.binhphuc.payment_service.kafka.constant.TopicConstant;
import com.binhphuc.payment_service.kafka.event.PaymentFailedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentFailedProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendPaymentFailedEvent(PaymentFailedEvent event) {
        kafkaTemplate.send(TopicConstant.PAYMENT_FAILED_TOPIC, event);
    }
}
