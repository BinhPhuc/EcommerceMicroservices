package com.binhphuc.flash_sale_service.schedule.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class SoldStatus {
    private String variantId;
    private boolean soldOut;
}
