package com.binhphuc.product_service.dto.product.request;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetProductByIdsRequest {
    @JsonProperty("product_ids")
    private List<String> productIds;
}
