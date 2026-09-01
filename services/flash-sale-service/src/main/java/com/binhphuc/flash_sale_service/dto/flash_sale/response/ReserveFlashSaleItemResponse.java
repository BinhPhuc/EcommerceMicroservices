package com.binhphuc.flash_sale_service.dto.flash_sale.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReserveFlashSaleItemResponse {
    @JsonProperty("flash_sale_item_id")
    private String flashSaleItemId;

    @JsonProperty("reserved_quantity")
    private Integer reservedQuantity;

    @JsonProperty("remaining_stock")
    private Long remainingStock;
}
