package com.binhphuc.payment_service.kafka.consumer;

import com.binhphuc.payment_service.kafka.command.ChargePaymentCommand;
import com.binhphuc.payment_service.kafka.constant.TopicConstant;
import com.binhphuc.payment_service.kafka.event.ChargePaymentEvent;
import com.binhphuc.payment_service.service.ChargePaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.BackOff;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChargePaymentConsumer {
    private final ChargePaymentService chargePaymentService;

    @KafkaListener(topics = TopicConstant.PAYMENT_CHARGE_PAYMENT_TOPIC)
    @RetryableTopic(
            attempts = "4",
            backOff = @BackOff(delay = 2000, multiplier = 2)
    )
    public void chargePayment(ChargePaymentEvent event) {
        ChargePaymentCommand command =
                ChargePaymentCommand.builder().idempotencyKey(event.getIdempotencyKey()).build();
        chargePaymentService.chargePayment(command);
    }
}
