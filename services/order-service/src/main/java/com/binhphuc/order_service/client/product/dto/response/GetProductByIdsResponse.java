package com.binhphuc.order_service.client.product.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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
