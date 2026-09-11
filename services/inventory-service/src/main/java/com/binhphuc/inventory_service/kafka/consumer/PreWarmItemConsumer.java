package com.binhphuc.inventory_service.kafka.consumer;

import com.binhphuc.inventory_service.kafka.command.CacheStockCommand;
import com.binhphuc.inventory_service.kafka.constant.TopicConstant;
import com.binhphuc.inventory_service.kafka.event.PreWarmItemEvent;
import com.binhphuc.inventory_service.service.PreWarmItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.BackOff;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PreWarmItemConsumer {
    private final PreWarmItemService preWarmItemService;

    @KafkaListener(topics = TopicConstant.PRE_WARM_ITEM_TOPIC)
    @RetryableTopic(
            attempts = "4",
            backOff = @BackOff(delay = 2000, multiplier = 2)
    )
    public void consumePreWarmItemEvent(PreWarmItemEvent event) {
        CacheStockCommand command = CacheStockCommand.builder()
                .flashSaleItems(event.getFlashSaleItems())
                .campaignId(event.getCampaignId())
                .build();
        preWarmItemService.preWarmItem(command);
    }
}