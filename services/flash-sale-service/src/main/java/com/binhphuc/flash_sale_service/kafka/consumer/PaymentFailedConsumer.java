package com.binhphuc.flash_sale_service.kafka.consumer;

import com.binhphuc.flash_sale_service.kafka.command.ReleaseStockCommand;
import com.binhphuc.flash_sale_service.kafka.constant.TopicConstant;
import com.binhphuc.flash_sale_service.kafka.event.PaymentFailedEvent;
import com.binhphuc.flash_sale_service.service.ReleaseStockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.BackOff;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentFailedConsumer {
    private final ReleaseStockService releaseStockService;

    @KafkaListener(topics = TopicConstant.PAYMENT_FAILED_TOPIC)
    @RetryableTopic(
            attempts = "4",
            backOff = @BackOff(delay = 2000, multiplier = 2)
    )
    public void consumePaymentFailedEvent(PaymentFailedEvent event) {
        ReleaseStockCommand command = ReleaseStockCommand.builder()
                .idempotencyKey(event.getIdempotencyKey())
                .campaignId(event.getCampaignId())
                .items(event.getItems())
                .build();
        releaseStockService.releaseStock(command);
    }
}
