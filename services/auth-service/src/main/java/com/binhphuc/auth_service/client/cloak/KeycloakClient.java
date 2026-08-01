package com.binhphuc.auth_service.client.cloak;

import com.binhphuc.auth_service.client.cloak.dto.request.OIDCDiscoveryRequest;
import com.binhphuc.auth_service.client.cloak.dto.request.TokenRequest;
import com.binhphuc.auth_service.client.cloak.dto.response.OIDCDiscoveryResponse;
import com.binhphuc.auth_service.client.cloak.dto.response.TokenResponse;

public interface KeycloakClient {
    OIDCDiscoveryResponse getOidcDiscovery(OIDCDiscoveryRequest oidcDiscoveryRequest);

    TokenResponse getToken(TokenRequest tokenRequest);
}
