package com.binhphuc.flash_sale_service.kafka.event.dto;

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
