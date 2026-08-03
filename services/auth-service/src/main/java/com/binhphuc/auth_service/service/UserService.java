package com.binhphuc.auth_service.service;

import com.binhphuc.auth_service.dto.auth.request.LoginRequest;
import com.binhphuc.auth_service.dto.auth.request.RegistrationRequest;
import com.binhphuc.auth_service.dto.auth.response.LoginResponse;

public interface UserService {
    void createUser(RegistrationRequest registrationRequest);

    LoginResponse auth(LoginRequest loginRequest);
}
