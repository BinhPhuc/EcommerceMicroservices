package com.binhphuc.payment_service.kafka.command;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChargePaymentCommand {
    private String orderId;
    private String idempotencyKey;
}
