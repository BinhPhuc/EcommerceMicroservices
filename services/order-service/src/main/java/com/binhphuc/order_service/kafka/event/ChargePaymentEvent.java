package com.binhphuc.order_service.kafka.event;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChargePaymentEvent {
    private String orderId;
    private String idempotencyKey;
}
