package com.springboot.cinema.service.impl;

import com.springboot.cinema.dto.RegisterFormDTO;
import com.springboot.cinema.entity.Customer;
import com.springboot.cinema.entity.Role;
import com.springboot.cinema.entity.User;
import com.springboot.cinema.entity.UserStatus;
import com.springboot.cinema.repository.UserRepository;
import com.springboot.cinema.service.RegisterService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegisterServiceImpl implements RegisterService {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;

    public RegisterServiceImpl(PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @Override
    public boolean isEmailExists(String email) {
        return (userRepository.findByEmail(email) != null);
    }

    @Transactional
    @Override
    public void registerUser(RegisterFormDTO registerFormDTO) {
        User user = new User(registerFormDTO.getEmail(),
                passwordEncoder.encode(registerFormDTO.getHashPassword()),
                registerFormDTO.getFullName(), registerFormDTO.getPhone(), Role.CUSTOMER, UserStatus.ACTIVE);

        Customer customer = new Customer(0);

        user.setCustomer(customer);
        customer.setUser(user);
        userRepository.save(user);
    }
}
