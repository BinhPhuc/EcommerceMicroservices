package com.binhphuc.product_service.kafka.consumer;

import com.binhphuc.product_service.kafka.event.dto.product.LockProductStockCommand;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.binhphuc.product_service.kafka.event.OrderCreatedEvent;
import com.binhphuc.product_service.service.ProductService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventConsumer {
    private final ProductService productService;

    @KafkaListener(topics = "order.created.v1")
    public void consumeOrderCreatedEvent(OrderCreatedEvent orderCreatedEvent) {
        log.info("Received OrderCreatedEvent: {}", orderCreatedEvent);
        LockProductStockCommand lockProductStockCommand = LockProductStockCommand.builder()
                .orderId(orderCreatedEvent.getOrderId())
                .orderItems(orderCreatedEvent.getOrderItems())
                .build();
        productService.lockProductStock(lockProductStockCommand);
    }
}
