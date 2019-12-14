package com.swaperclone.service;

import com.swaperclone.dto.RegistrationDTO;
import com.swaperclone.entity.User;
import com.swaperclone.exception.NotFoundException;
import com.swaperclone.repository.UserRepository;
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
