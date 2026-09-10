package com.binhphuc.order_service.kafka.consumer;

import com.binhphuc.order_service.kafka.command.CreateFlashSaleOrderCommand;
import com.binhphuc.order_service.kafka.constant.TopicConstant;
import com.binhphuc.order_service.kafka.event.FlashSaleCreateOrderEvent;
import com.binhphuc.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.BackOff;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FlashSaleOrderConsumer {
    private final OrderService orderService;

    @KafkaListener(topics = TopicConstant.FLASH_SALE_CREATE_ORDER_TOPIC)
    @RetryableTopic(
            attempts = "4",
            backOff = @BackOff(delay = 2000, multiplier = 2)
    )
    public void createFlashSaleOrder(FlashSaleCreateOrderEvent event) {
        orderService.createFlashSaleOrder(CreateFlashSaleOrderCommand.builder()
                .userId(event.getUserId())
                .requestId(event.getRequestId())
                .campaignId(event.getCampaignId())
                .items(event.getItems())
                .build());
    }
}
