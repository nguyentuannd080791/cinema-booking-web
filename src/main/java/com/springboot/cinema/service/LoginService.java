package com.springboot.cinema.service;

import com.springboot.cinema.dto.UserInformationDTO;
import com.springboot.cinema.entity.User;

public interface LoginService {
    UserInformationDTO login(String email, String password);
}
