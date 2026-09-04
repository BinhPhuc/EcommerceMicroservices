package com.binhphuc.order_service.kafka.producer;

import com.binhphuc.order_service.kafka.constant.TopicConstant;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.binhphuc.order_service.kafka.event.OrderCreatedEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderEventProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendOrderCreatedEvent(OrderCreatedEvent orderCreatedEvent) {
        kafkaTemplate.send(TopicConstant.ORDER_CREATE_TOPIC, orderCreatedEvent);
    }
}
