package com.example.mnclone.service;

import com.example.mnclone.dto.RegistrationDTO;
import com.example.mnclone.entity.User;
import com.example.mnclone.exception.NotFoundException;
import com.example.mnclone.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public void register(RegistrationDTO registrationDTO) {
        User user = new User();
        String registrationToken = UUID.randomUUID().toString();
        user.setEmail(registrationDTO.getEmail());
        user.setEncodedPassword(passwordEncoder.encode(registrationDTO.getPassword()));
        user.setFirstName(registrationDTO.getFirstName());
        user.setLastName(registrationDTO.getLastName());
        user.setBalance(BigDecimal.ZERO);
        user.setRegistered(false);
        user.setRegistrationToken(registrationToken);
        userRepository.save(user);
        emailService.sendWelcomeEmail(registrationDTO.getEmail(), registrationToken);
    }

    public void completeRegistration(String token) {
        User user = userRepository.findByRegistrationToken(token)
                .orElseThrow(() -> new NotFoundException("User not found"));
        user.setRegistered(true);
        userRepository.save(user);
    }

}
