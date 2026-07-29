package com.binhphuc.auth_service.service;

import com.binhphuc.auth_service.dto.auth.UserRegistrationDTO;

public interface UserService {
    void createUser(UserRegistrationDTO userRegistrationDTO);
}
