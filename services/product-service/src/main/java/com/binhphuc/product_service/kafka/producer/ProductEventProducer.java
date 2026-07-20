package com.binhphuc.product_service.kafka.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;


import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductEventProducer {
    private final String PRODUCT_LOCKED_TOPIC = "product.lock-stock.v1";
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendLockProductStockEvent(Object event) {
        kafkaTemplate.send(PRODUCT_LOCKED_TOPIC, event);
    }
}
