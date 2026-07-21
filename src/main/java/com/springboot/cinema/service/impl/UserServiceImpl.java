package com.springboot.cinema.service.impl;

import com.springboot.cinema.dto.RegisterFormDTO;
import com.springboot.cinema.dto.UserInformationDTO;
import com.springboot.cinema.entity.Customer;
import com.springboot.cinema.entity.Role;
import com.springboot.cinema.entity.User;
import com.springboot.cinema.entity.UserStatus;
import com.springboot.cinema.repository.UserRepository;
import com.springboot.cinema.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserInformationDTO login(String email, String password) {
        User user = userRepository.findByEmail(email);

        if (user == null || !passwordEncoder.matches(password, user.getHashPassword())) return null;
        if (user.getStatus() != UserStatus.ACTIVE) return null;

        return new UserInformationDTO(user.getId(), user.getFullName(), user.getRole());
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
