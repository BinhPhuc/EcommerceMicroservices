package com.binhphuc.flash_sale_service.dto.flash_sale.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateFlashSaleRequest {
    @NotEmpty
    private String name;

    private String description;

    @JsonProperty("started_at")
    @NotNull
    private Instant startedAt;

    @JsonProperty("ended_at")
    @NotNull
    private Instant endedAt;

    @Valid
    @NotEmpty
    private List<CreateFlashSaleItemRequest> items;
}
