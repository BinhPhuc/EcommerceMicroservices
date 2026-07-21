package com.binhphuc.order_service.kafka.consumer;

import com.binhphuc.order_service.kafka.event.dto.order.ChangeOrderStatusCommand;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.binhphuc.order_service.enums.OrderStatus;
import com.binhphuc.order_service.kafka.event.ProductLockedEvent;
import com.binhphuc.order_service.service.OrderService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductLockConsumer {
    private final String PRODUCT_LOCKED_TOPIC = "product.lock-stock.v1";
    private final OrderService orderService;

    @KafkaListener(topics = PRODUCT_LOCKED_TOPIC)
    public void consumeOrderCreatedEvent(ProductLockedEvent productLockedEvent) {
        orderService.changeOrderStatus(ChangeOrderStatusCommand.builder()
                .orderId(productLockedEvent.getOrderId())
                .orderStatus(OrderStatus.PREPARED)
                .build());
    }
}

