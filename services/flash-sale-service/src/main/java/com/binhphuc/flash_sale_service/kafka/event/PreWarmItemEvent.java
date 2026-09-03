package com.binhphuc.flash_sale_service.kafka.event;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PreWarmItemEvent {
    private List<String> productIds;
    private List<String> variantIds;
}
