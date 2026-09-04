package com.binhphuc.flash_sale_service.client.product.dto.request;

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
