package com.binhphuc.flash_sale_service.kafka.event.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FlashSaleOrderItem {
    private String productId;
    private String variantId;
    private Integer quantity;
    private BigDecimal price;
}
