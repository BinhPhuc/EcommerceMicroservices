package com.binhphuc.product_service.kafka.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.binhphuc.product_service.kafka.event.ProductLockedEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductEventProducer {
    private final String PRODUCT_LOCKED_TOPIC = "product.lock-stock.v1";
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendLockProductStockEvent(ProductLockedEvent productLockedEvent) {
        kafkaTemplate.send(PRODUCT_LOCKED_TOPIC, productLockedEvent);
    }
}
