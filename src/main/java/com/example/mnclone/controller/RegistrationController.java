package com.example.mnclone.controller;

import com.example.mnclone.dto.RegistrationDTO;
import com.example.mnclone.dto.validation.RegistrationDTOValidator;
import com.example.mnclone.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@PreAuthorize("isAnonymous()")
@RequestMapping("/api/register")
public class RegistrationController {

    private final UserService userService;
    private final RegistrationDTOValidator registrationDTOValidator;

    @Autowired
    public RegistrationController(UserService userService, RegistrationDTOValidator registrationDTOValidator) {
        this.userService = userService;
        this.registrationDTOValidator = registrationDTOValidator;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.addValidators(registrationDTOValidator);
    }

    @PostMapping
    public void register(@Valid @RequestBody RegistrationDTO registrationDTO) {
        userService.register(registrationDTO);
    }

    @GetMapping
    public void completeRegistration(@RequestParam String token) {
        userService.completeRegistration(token);
        //TODO maybe redirect
    }

}
