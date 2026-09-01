package com.binhphuc.flash_sale_service.kafka.event;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class FlashSaleItemReservedEvent {
    private String flashSaleId;
    private String flashSaleItemId;
    private String productId;
    private String variantId;
    private String userId;
    private BigDecimal flashPrice;
    private Integer quantity;
}
