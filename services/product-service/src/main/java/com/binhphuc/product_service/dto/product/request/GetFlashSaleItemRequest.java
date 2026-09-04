package com.binhphuc.product_service.dto.product.request;

import com.binhphuc.product_service.dto.falshsale.FlashSaleItem;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetFlashSaleItemRequest {
    @JsonProperty("flash_sale_items")
    List<FlashSaleItem> flashSaleItems;

    @JsonIgnore
    public String getCacheKey() {
        return flashSaleItems.stream().map(item -> item.getProductId()).sorted().collect(Collectors.joining(","));
    }
}
