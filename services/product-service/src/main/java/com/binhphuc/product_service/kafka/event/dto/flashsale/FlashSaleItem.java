package com.binhphuc.product_service.kafka.event.dto.flashsale;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FlashSaleItem {
    private String productId;
    private String variantId;
}
