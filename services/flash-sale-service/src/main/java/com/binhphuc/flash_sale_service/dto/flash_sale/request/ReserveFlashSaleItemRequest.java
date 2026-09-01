package com.binhphuc.flash_sale_service.dto.flash_sale.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReserveFlashSaleItemRequest {
    @JsonProperty("flash_sale_item_id")
    @NotEmpty
    private String flashSaleItemId;

    @NotNull
    @Positive
    private Integer quantity;
}
