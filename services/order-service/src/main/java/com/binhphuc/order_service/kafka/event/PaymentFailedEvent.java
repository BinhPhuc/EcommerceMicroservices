package com.binhphuc.order_service.kafka.event;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentFailedEvent {
    private String orderId;
}
