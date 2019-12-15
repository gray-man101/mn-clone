package com.swaperclone.customer.service;

import com.swaperclone.customer.dto.RegistrationDTO;
import com.swaperclone.common.entity.User;
import com.swaperclone.common.exception.NotFoundException;
import com.swaperclone.common.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public String register(RegistrationDTO registrationDTO) {
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
        return registrationToken;
    }

    @Transactional
    public void completeRegistration(String token) {
        int updatedObjects = userRepository.markUserAsRegistered(token);
        if (updatedObjects < 1) {
            throw new NotFoundException(String.format("User with token %s not found", token));
        }
    }

}
