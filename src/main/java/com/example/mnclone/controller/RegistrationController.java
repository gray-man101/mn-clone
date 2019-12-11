package com.example.mnclone.controller;

import com.example.mnclone.dto.RegistrationDTO;
import com.example.mnclone.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/register")
public class RegistrationController {

    @Autowired
    private UserService userService;

    @PostMapping
    public void register(@Valid @RequestBody RegistrationDTO registrationDTO) {
        userService.registerCustomer(registrationDTO);
    }

}
