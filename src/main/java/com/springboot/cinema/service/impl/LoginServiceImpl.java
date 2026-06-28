package com.springboot.cinema.service.impl;

import com.springboot.cinema.dto.UserInformationDTO;
import com.springboot.cinema.entity.User;
import com.springboot.cinema.repository.UserRepository;
import com.springboot.cinema.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class LoginServiceImpl implements LoginService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserInformationDTO login(String email, String password) {
        User user = userRepository.findByEmail(email);

        if (user == null || !passwordEncoder.matches(password, user.getHashPassword())) return null;

        return new UserInformationDTO(user.getId(), user.getFullName(), user.getRole());
    }
}
