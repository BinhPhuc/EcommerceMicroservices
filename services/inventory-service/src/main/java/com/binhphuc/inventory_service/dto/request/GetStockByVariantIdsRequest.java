package com.binhphuc.inventory_service.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetStockByVariantIdsRequest {
    @JsonProperty("variant_ids")
    private List<String> variantIds;

    @JsonIgnore
    public String getCacheKey() {
        return variantIds.stream().sorted().collect(Collectors.joining(","));
    }
}
