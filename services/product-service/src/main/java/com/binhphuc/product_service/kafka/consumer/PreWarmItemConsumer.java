package com.binhphuc.product_service.kafka.consumer;

import com.binhphuc.product_service.kafka.command.PreWarmItemCommand;
import com.binhphuc.product_service.kafka.constant.TopicConstant;
import com.binhphuc.product_service.kafka.event.PreWarmItemEvent;
import com.binhphuc.product_service.service.PreWarmItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.BackOff;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class PreWarmItemConsumer {
    private final PreWarmItemService preWarmItemService;

    @KafkaListener(topics = TopicConstant.PRE_WARM_ITEM_TOPIC)
    @RetryableTopic(
            attempts = "4",
            backOff = @BackOff(delay = 2000, multiplier = 2),
            exclude = {IllegalArgumentException.class, IllegalStateException.class}
    )
    public void consumePreWarmItemEvent(PreWarmItemEvent event) {
        preWarmItemService.preWarmItem(PreWarmItemCommand.builder().flashSaleItems(event.getFlashSaleItems()).campaignId(event.getCampaignId()).build());
    }
}
