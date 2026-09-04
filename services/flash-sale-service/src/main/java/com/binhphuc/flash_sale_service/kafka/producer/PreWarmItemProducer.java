package com.binhphuc.flash_sale_service.kafka.producer;

import com.binhphuc.flash_sale_service.kafka.constant.TopicConstant;
import com.binhphuc.flash_sale_service.kafka.event.PreWarmItemEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PreWarmItemProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendPreWarmItemEvent(PreWarmItemEvent event) {
        kafkaTemplate.send(TopicConstant.FLASH_SALE_PRE_WARM_TOPIC, event);
    }
}
