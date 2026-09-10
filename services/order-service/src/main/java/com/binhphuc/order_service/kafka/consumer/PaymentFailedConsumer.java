package com.binhphuc.order_service.kafka.consumer;

import com.binhphuc.order_service.enums.OrderStatus;
import com.binhphuc.order_service.kafka.command.ChangeOrderStatusCommand;
import com.binhphuc.order_service.kafka.constant.TopicConstant;
import com.binhphuc.order_service.kafka.event.PaymentFailedEvent;
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
public class PaymentFailedConsumer {
    private final OrderService orderService;

    @KafkaListener(topics = TopicConstant.FLASH_SALE_CREATE_ORDER_TOPIC)
    @RetryableTopic(
            attempts = "4",
            backOff = @BackOff(delay = 2000, multiplier = 2)
    )
    public void consumePaymentFailedEvent(PaymentFailedEvent event) {
        ChangeOrderStatusCommand command = ChangeOrderStatusCommand.builder()
                .orderId(event.getOrderId())
                .status(OrderStatus.CANCELED)
                .build();
        orderService.changeOrderStatus(command);
    }
}
