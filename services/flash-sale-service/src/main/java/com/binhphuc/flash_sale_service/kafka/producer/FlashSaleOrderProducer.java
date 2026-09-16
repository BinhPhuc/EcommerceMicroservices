package com.binhphuc.flash_sale_service.kafka.producer;

import com.binhphuc.flash_sale_service.kafka.constant.TopicConstant;
import com.binhphuc.flash_sale_service.kafka.event.FlashSaleCreateOrderEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class FlashSaleOrderProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public CompletableFuture<SendResult<String, Object>> sendFlashSaleOrderCreatedEvent(FlashSaleCreateOrderEvent event) {
        return kafkaTemplate.send(TopicConstant.FLASH_SALE_CREATE_ORDER_TOPIC,
                event.getRequestId(), event);
    }
}