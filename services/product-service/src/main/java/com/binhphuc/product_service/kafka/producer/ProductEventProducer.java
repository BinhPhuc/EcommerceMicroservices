package com.binhphuc.product_service.kafka.producer;

import com.binhphuc.product_service.kafka.constant.TopicConstant;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.binhphuc.product_service.kafka.event.ProductLockedEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductEventProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendLockProductStockEvent(ProductLockedEvent productLockedEvent) {
        kafkaTemplate.send(TopicConstant.PRODUCT_LOCKED_TOPIC, productLockedEvent);
    }
}
