package com.springboot.cinema.service;

import com.springboot.cinema.dto.RegisterFormDTO;

public interface RegisterService {
    boolean isEmailExists(String email);

    void registerUser(RegisterFormDTO registerFormDTO);
}
