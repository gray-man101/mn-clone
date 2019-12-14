package com.swaperclone.controller;

import com.swaperclone.dto.RegistrationDTO;
import com.swaperclone.dto.validation.RegistrationDTOValidator;
import com.swaperclone.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;

@RestController
@PreAuthorize("isAnonymous()")
@RequestMapping("/api/register")
public class RegistrationController {

    @Value("${app.home.url:http://localhost:8080/}")
    private String homeUrl;

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
    public void completeRegistration(@RequestParam String token, HttpServletResponse response) throws IOException {
        userService.completeRegistration(token);
        response.sendRedirect(this.homeUrl);
    }

}
