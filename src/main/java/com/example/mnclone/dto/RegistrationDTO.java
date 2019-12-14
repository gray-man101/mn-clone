package com.example.mnclone.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class RegistrationDTO {
    @NotBlank(message = "First name cannot be blank")
    private String firstName;
    @NotBlank(message = "Last name cannot be blank")
    private String lastName;
    @NotBlank(message = "Password cannot be blank")
    private String password;
    @NotBlank(message = "Password cannot be blank")
    private String passwordRepeat;
    @NotBlank(message = "Email cannot be blank")
    @Email
    private String email;
}
