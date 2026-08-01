package com.binhphuc.auth_service.service.impl;

import java.util.Collections;

import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.ErrorRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.binhphuc.auth_service.client.cloak.KeycloakClient;
import com.binhphuc.auth_service.client.cloak.dto.request.OIDCDiscoveryRequest;
import com.binhphuc.auth_service.client.cloak.dto.request.TokenRequest;
import com.binhphuc.auth_service.client.cloak.dto.response.OIDCDiscoveryResponse;
import com.binhphuc.auth_service.client.cloak.dto.response.TokenResponse;
import com.binhphuc.auth_service.dto.auth.request.LoginRequest;
import com.binhphuc.auth_service.dto.auth.request.RegistrationRequest;
import com.binhphuc.auth_service.dto.auth.response.LoginResponse;
import com.binhphuc.auth_service.service.UserService;
import com.binhphuc.common_web_starter.exception.BusinessException;

import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.clientId}")
    private String clientId;

    @Value("${keycloak.clientSecret}")
    private String clientSecret;

    private final String grantType = "password";
    private final Keycloak keycloak;
    private final KeycloakClient keycloakClient;

    @Override
    public void createUser(RegistrationRequest registrationRequest) {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(registrationRequest.getUsername());
        user.setEmail(registrationRequest.getEmail());
        user.setEnabled(true);
        user.setFirstName(registrationRequest.getFirstName());
        user.setLastName(registrationRequest.getLastName());

        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        credentialRepresentation.setValue(registrationRequest.getPassword());
        user.setCredentials(Collections.singletonList(credentialRepresentation));

        RealmResource realmResource = keycloak.realm(realm);
        UsersResource usersResource = realmResource.users();
        Response response = usersResource.create(user);

        if (response == null) {
            throw new BusinessException(HttpStatus.BAD_GATEWAY, "Keycloak service is not available");
        }

        int statusCode = response.getStatus();

        if (statusCode != 201) {
            ErrorRepresentation errorRepresentation = response.readEntity(ErrorRepresentation.class);
            String errorMessage = errorRepresentation != null ? errorRepresentation.getErrorMessage() : "Unknown error";
            throw new BusinessException(HttpStatus.valueOf(statusCode), errorMessage);
        }

        String userId = CreatedResponseUtil.getCreatedId(response);
        UserResource newUser = keycloak.realm(realm).users().get(userId);
        RoleRepresentation roleRepresentation = realmResource
                .roles()
                .get(registrationRequest.getRole())
                .toRepresentation();
        newUser.roles().realmLevel().add(Collections.singletonList(roleRepresentation));

        response.close();
    }

    @Override
    public LoginResponse auth(LoginRequest loginRequest) {
        OIDCDiscoveryResponse oidcDiscoveryResponse = keycloakClient
                .getOidcDiscovery(OIDCDiscoveryRequest.builder().realm(realm).build());

        TokenResponse tokenResponse = keycloakClient
                .getToken(TokenRequest
                        .builder()
                        .clientId(clientId)
                        .clientSecret(clientSecret)
                        .username(loginRequest.getUsername())
                        .password(loginRequest.getPassword())
                        .tokenEndpoint(oidcDiscoveryResponse.getTokenEndpoint())
                        .grantType(grantType)
                        .build());

        return LoginResponse
                .builder()
                .accessToken(tokenResponse.getAccessToken())
                .refreshToken(tokenResponse.getRefreshToken())
                .expiresIn(tokenResponse.getExpiresIn())
                .refreshExpiresIn(tokenResponse.getRefreshExpiresIn())
                .build();
    }
}
