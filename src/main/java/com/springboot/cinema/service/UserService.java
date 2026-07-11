package com.springboot.cinema.service;

import com.springboot.cinema.dto.RegisterFormDTO;
import com.springboot.cinema.dto.UserInformationDTO;

public interface UserService {
    UserInformationDTO login(String email, String password);

    boolean isEmailExists(String email);

    void registerUser(RegisterFormDTO registerFormDTO);
}
