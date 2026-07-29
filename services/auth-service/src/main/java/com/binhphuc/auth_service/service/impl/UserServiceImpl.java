package com.binhphuc.auth_service.service.impl;

import java.util.Collections;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.binhphuc.auth_service.dto.auth.UserRegistrationDTO;
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
    public void createUser(UserRegistrationDTO userRegistrationDTO) {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(userRegistrationDTO.getUsername());
        user.setEmail(userRegistrationDTO.getEmail());
        user.setEnabled(true);
        user.setFirstName(userRegistrationDTO.getFirstName());
        user.setLastName(userRegistrationDTO.getLastName());

        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        credentialRepresentation.setValue(userRegistrationDTO.getPassword());
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
}
