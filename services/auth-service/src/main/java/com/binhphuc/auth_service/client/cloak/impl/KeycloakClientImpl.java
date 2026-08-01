package com.binhphuc.auth_service.client.cloak.impl;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.binhphuc.auth_service.client.cloak.KeycloakClient;
import com.binhphuc.auth_service.client.cloak.dto.request.OIDCDiscoveryRequest;
import com.binhphuc.auth_service.client.cloak.dto.request.TokenRequest;
import com.binhphuc.auth_service.client.cloak.dto.response.AuthErrorResponse;
import com.binhphuc.auth_service.client.cloak.dto.response.OIDCDiscoveryResponse;
import com.binhphuc.auth_service.client.cloak.dto.response.TokenResponse;
import com.binhphuc.common_web_starter.exception.BusinessException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class KeycloakClientImpl implements KeycloakClient {
    private final WebClient keycloakClient;

    @Override
    public OIDCDiscoveryResponse getOidcDiscovery(OIDCDiscoveryRequest oidcDiscoveryRequest) {
        OIDCDiscoveryResponse response = keycloakClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/realms/{realm}/.well-known/openid-configuration")
                        .build(oidcDiscoveryRequest.getRealm()))
                .retrieve()
                .bodyToMono(OIDCDiscoveryResponse.class)
                .block();
        if (response == null) {
            throw new BusinessException(HttpStatus.BAD_GATEWAY, "Keycloak service is not available");
        }

        return response;
    }

    @Override
    public TokenResponse getToken(TokenRequest tokenRequest) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("client_id", tokenRequest.getClientId());
        formData.add("client_secret", tokenRequest.getClientSecret());
        formData.add("grant_type", tokenRequest.getGrantType());
        formData.add("username", tokenRequest.getUsername());
        formData.add("password", tokenRequest.getPassword());

        TokenResponse tokenResponse = keycloakClient
                .post()
                .uri(URI.create(tokenRequest.getTokenEndpoint()))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> response
                        .bodyToMono(AuthErrorResponse.class)
                        .defaultIfEmpty(new AuthErrorResponse())
                        .map(errorResponse -> new BusinessException(HttpStatus.valueOf(response.statusCode().value()),
                                errorResponse.getErrorDescription() != null ? errorResponse.getErrorDescription() :
                                        "Failed to get token")))
                .bodyToMono(TokenResponse.class)
                .block();

        if (tokenResponse == null) {
            throw new BusinessException(HttpStatus.BAD_GATEWAY, "Keycloak service is not available");
        }

        return tokenResponse;
    }
}
