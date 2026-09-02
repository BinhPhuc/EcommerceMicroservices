package com.binhphuc.flash_sale_service.dto.flash_sale.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateCampaignItemRequest {
    @JsonProperty("product_id")
    @NotEmpty
    private String productId;

    @JsonProperty("variant_id")
    @NotEmpty
    private String variantId;

    @NotNull
    @Positive
    private BigDecimal price;

    @NotNull
    @Positive
    private Long stock;
}
