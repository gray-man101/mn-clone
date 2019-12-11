package com.example.mnclone.service;

import com.example.mnclone.dto.RegistrationDTO;
import com.example.mnclone.entity.User;
import com.example.mnclone.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public void registerCustomer(RegistrationDTO registrationDTO) {
        User user = new User();
        user.setEmail(registrationDTO.getEmail());
        user.setPasswordHash(passwordEncoder.encode(registrationDTO.getPassword()));
        user.setFirstName(registrationDTO.getFirstName());
        user.setLastName(registrationDTO.getLastName());
        user.setBalance(BigDecimal.ZERO);
        user.setRegistered(false);
        userRepository.save(user);
        emailService.sendWelcomeEmail(registrationDTO.getEmail());
    }

}
