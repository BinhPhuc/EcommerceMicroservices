package com.binhphuc.order_service.schedule;

import com.binhphuc.order_service.entity.OrderOutbox;
import com.binhphuc.order_service.entity.OrderOutboxVariant;
import com.binhphuc.order_service.kafka.event.ChargePaymentEvent;
import com.binhphuc.order_service.kafka.event.dto.order.StockItem;
import com.binhphuc.order_service.kafka.producer.ChargePaymentProducer;
import com.binhphuc.order_service.service.OrderOutboxService;
import com.binhphuc.order_service.service.OrderOutboxVariantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderOutboxRelay {
    private static final int DEFAULT_QUANTITY = 1;

    private final ChargePaymentProducer chargePaymentProducer;
    private final OrderOutboxService orderOutboxService;
    private final OrderOutboxVariantService orderOutboxVariantService;

    @Scheduled(fixedDelayString = "${order.outbox.relay-delay}:10000")
    public void processOrderOutbox() {
        List<OrderOutbox> unProcessedOutbox = orderOutboxService.getListUnprocessedOrderOutbox();
        for (OrderOutbox outbox : unProcessedOutbox) {
            List<StockItem> items = orderOutboxVariantService.findAllByOutboxId(outbox.getId())
                    .stream()
                    .map(variant -> StockItem.builder()
                            .variantId(variant.getVariantId())
                            .quantity(resolveQuantity(outbox, variant))
                            .build())
                    .toList();
            ChargePaymentEvent chargePaymentEvent = ChargePaymentEvent
                    .builder()
                    .orderId(outbox.getOrderId())
                    .idempotencyKey(outbox.getId())
                    .campaignId(outbox.getCampaignId())
                    .items(items)
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

    // Rows written before the quantity column existed have a null quantity. Sending it downstream
    // would break the compensating release, which turns the quantity into a Redis INCRBY argument.
    private int resolveQuantity(OrderOutbox outbox, OrderOutboxVariant variant) {
        Integer quantity = variant.getQuantity();
        if (quantity == null || quantity <= 0) {
            log.warn("Outbox variant id: {} of orderId: {} has invalid quantity: {}, falling back "
                    + "to {}", variant.getId(), outbox.getOrderId(), quantity, DEFAULT_QUANTITY);
            return DEFAULT_QUANTITY;
        }
        return quantity;
    }
}
