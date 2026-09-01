package com.binhphuc.flash_sale_service.kafka.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.binhphuc.flash_sale_service.kafka.event.FlashSaleItemReservedEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FlashSaleEventProducer {
    private final String FLASH_SALE_ITEM_RESERVED_TOPIC = "flash-sale.item-reserved.v1";
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendFlashSaleItemReservedEvent(FlashSaleItemReservedEvent flashSaleItemReservedEvent) {
        kafkaTemplate.send(FLASH_SALE_ITEM_RESERVED_TOPIC, flashSaleItemReservedEvent);
    }
}
