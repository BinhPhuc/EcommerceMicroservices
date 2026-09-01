package com.binhphuc.product_service.dto.product.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateProductImageRequest {
    private String url;

    @JsonProperty("is_thumbnail")
    private Boolean isThumbnail;

    @JsonProperty("display_order")
    private Integer displayOrder;
}
