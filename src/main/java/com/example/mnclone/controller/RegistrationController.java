package com.example.mnclone.controller;

import com.example.mnclone.dto.RegistrationDTO;
import com.example.mnclone.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/register")
public class RegistrationController {

    @Autowired
    private UserService userService;

    @PostMapping
    public void register(@Valid @RequestBody RegistrationDTO registrationDTO) {
        userService.register(registrationDTO);
    }

    @GetMapping
    public void completeRegistration(@RequestParam String token) {
        userService.completeRegistration(token);
        //TODO redirect
    }

}
