package com.binhphuc.product_service.dto.product.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetProductByIdsResponse {
    private String id;

    private String name;

    private Integer price;

    private Integer stock;

    @JsonProperty("category_id")
    private String categoryId;

    @JsonProperty("is_deleted")
    private Boolean isDeleted;
}
