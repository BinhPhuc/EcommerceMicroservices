package com.binhphuc.auth_service.client.cloak.dto.request;

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
public class TokenRequest {
    private String clientId;

    private String clientSecret;

    private String grantType;

    private String username;

    private String password;

    private String tokenEndpoint;
}
