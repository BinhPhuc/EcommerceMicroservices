package com.binhphuc.product_service.dto.product.request;

import lombok.*;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductVariantRequest {
    private String sku;

    private Map<String, String> attributes;

    private BigDecimal price;
}
