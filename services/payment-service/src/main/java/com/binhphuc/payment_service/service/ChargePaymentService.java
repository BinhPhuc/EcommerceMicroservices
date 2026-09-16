package com.binhphuc.payment_service.service;

import com.binhphuc.payment_service.kafka.command.ChargePaymentCommand;

public interface ChargePaymentService {
    void chargePayment(ChargePaymentCommand command);
}
