package com.binhphuc.order_service.schedule;

import com.binhphuc.order_service.entity.OrderOutbox;
import com.binhphuc.order_service.kafka.event.ChargePaymentEvent;
import com.binhphuc.order_service.kafka.producer.ChargePaymentProducer;
import com.binhphuc.order_service.service.OrderOutboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderOutboxRelay {
    private final ChargePaymentProducer chargePaymentProducer;
    private final OrderOutboxService orderOutboxService;

    @Scheduled(fixedDelayString = "${order.outbox.relay-delay}:10000")
    public void processOrderOutbox() {
        List<OrderOutbox> unProcessedOutbox = orderOutboxService.getListUnprocessedOrderOutbox();
        for (OrderOutbox outbox : unProcessedOutbox) {
            ChargePaymentEvent chargePaymentEvent = ChargePaymentEvent
                    .builder()
                    .orderId(outbox.getOrderId())
                    .idempotencyKey(outbox.getId())
                    .build();
            CompletableFuture<SendResult<String, Object>> future =
                    chargePaymentProducer.chargePayment(chargePaymentEvent);
            future.whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("Failed to send ChargePaymentEvent for orderId: {}",
                            outbox.getOrderId(), ex);
                } else {
                    orderOutboxService.setOutboxProcessed(outbox);
                    log.info("Successfully sent ChargePaymentEvent for orderId: {}",
                            outbox.getOrderId());
                }
            });
        }
    }
}
