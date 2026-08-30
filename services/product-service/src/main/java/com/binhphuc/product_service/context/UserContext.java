package com.binhphuc.product_service.context;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserContext {
    private String username;
    private String userId;
}
