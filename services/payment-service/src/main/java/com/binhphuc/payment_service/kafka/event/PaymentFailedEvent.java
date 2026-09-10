package com.binhphuc.payment_service.kafka.event;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentFailedEvent {
    private String orderId;
}
