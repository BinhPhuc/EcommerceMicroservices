package com.binhphuc.flash_sale_service.dto.flash_sale.response;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class CreateCampaignResponse {
    private String id;

    private String name;

    @JsonProperty("started_at")
    private Instant startedAt;

    @JsonProperty("ended_at")
    private Instant endedAt;
}
