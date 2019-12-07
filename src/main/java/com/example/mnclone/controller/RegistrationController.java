package com.example.mnclone.controller;

import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

@RestController
@RequestMapping("api/register")
@Secured("isAnonymous()")
public class RegistrationController {

    @PostMapping
    public void register() {
        throw new NotImplementedException();
    }

}
