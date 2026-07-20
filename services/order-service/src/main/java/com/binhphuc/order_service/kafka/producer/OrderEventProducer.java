package com.binhphuc.order_service.kafka.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.binhphuc.order_service.kafka.event.OrderCreatedEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderEventProducer {
    private final String ORDER_CREATED_TOPIC = "order.created.v1";
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendOrderCreatedEvent(OrderCreatedEvent orderCreatedEvent) {
        kafkaTemplate.send(ORDER_CREATED_TOPIC, orderCreatedEvent);
    }
}
