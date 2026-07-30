package com.binhphuc.auth_service.service.impl;

import java.util.Collections;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

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

    private final Keycloak keycloak;

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

        Response response = keycloak.realm(realm).users().create(user);
        if (response == null) {
            throw new BusinessException(HttpStatus.BAD_GATEWAY, "Failed to create user");
        }
        if (response.getStatus() != 201) {
            int statusCode = response.getStatus();
            HttpStatus httpStatus = HttpStatus.valueOf(statusCode);
            throw new BusinessException(httpStatus, "Failed to create user");
        }
    }

    @Override
    public LoginResponse auth(LoginRequest loginRequest) {

        return null;
    }
}
