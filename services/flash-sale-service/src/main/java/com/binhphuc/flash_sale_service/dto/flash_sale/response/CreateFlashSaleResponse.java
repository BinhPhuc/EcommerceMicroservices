package com.binhphuc.flash_sale_service.dto.flash_sale.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.binhphuc.flash_sale_service.enums.FlashSaleStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateFlashSaleResponse {
    private String id;

    private String name;

    private FlashSaleStatus status;

    @JsonProperty("started_at")
    private Instant startedAt;

    @JsonProperty("ended_at")
    private Instant endedAt;
}
