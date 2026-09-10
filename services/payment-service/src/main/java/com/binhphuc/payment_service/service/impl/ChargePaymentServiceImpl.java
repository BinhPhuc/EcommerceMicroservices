package com.binhphuc.payment_service.service.impl;

import com.binhphuc.payment_service.entity.ProcessedEvent;
import com.binhphuc.payment_service.kafka.command.ChargePaymentCommand;
import com.binhphuc.payment_service.repository.ProcessedEventRepository;
import com.binhphuc.payment_service.service.ChargePaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChargePaymentServiceImpl implements ChargePaymentService {
    private final ProcessedEventRepository processedEventRepository;

    @Override
    public void chargePayment(ChargePaymentCommand command) {
        if (processedEventRepository.existsByIdempotencyKey(command.getIdempotencyKey())) {
            return;
        }
        // TODO: payment logic is dependent on payment gateway, so we will skip it for now
        ProcessedEvent processedEvent =
                ProcessedEvent.builder().idempotencyKey(command.getIdempotencyKey()).build();
        processedEventRepository.save(processedEvent);
    }
}
