package com.binhphuc.payment_service.service.impl;

import com.binhphuc.payment_service.entity.ProcessedEvent;
import com.binhphuc.payment_service.kafka.command.ChargePaymentCommand;
import com.binhphuc.payment_service.kafka.event.PaymentFailedEvent;
import com.binhphuc.payment_service.kafka.producer.PaymentFailedProducer;
import com.binhphuc.payment_service.repository.ProcessedEventRepository;
import com.binhphuc.payment_service.service.ChargePaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChargePaymentServiceImpl implements ChargePaymentService {
    private final ProcessedEventRepository processedEventRepository;
    private final PaymentFailedProducer paymentFailedProducer;

    @Override
    public void chargePayment(ChargePaymentCommand command) {
        boolean isError = false;
        if (isError) {
            PaymentFailedEvent event = PaymentFailedEvent.builder()
                    .orderId(command.getOrderId())
                    .idempotencyKey(command.getIdempotencyKey())
                    .campaignId(command.getCampaignId())
                    .items(command.getItems())
                    .build();
            paymentFailedProducer.sendPaymentFailedEvent(event);
        } else {
            if (processedEventRepository.existsByIdempotencyKey(command.getIdempotencyKey())) {
                return;
            }
            ProcessedEvent processedEvent =
                    ProcessedEvent.builder().idempotencyKey(command.getIdempotencyKey()).build();
            processedEventRepository.save(processedEvent);
            log.info("Payment charged successfully for idempotencyKey: {}",
                    command.getIdempotencyKey());
        }
    }
}