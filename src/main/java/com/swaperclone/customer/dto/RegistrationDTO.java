package com.swaperclone.customer.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class RegistrationDTO {
    @NotBlank(message = "First name cannot be blank")
    private String firstName;
    @NotBlank(message = "Last name cannot be blank")
    private String lastName;
    @NotBlank(message = "Password repeat cannot be blank")
    @Length(min = 5, message = "Password must be at least 5 chars long")
    private String password;
    @NotBlank(message = "Password repeat cannot be blank")
    private String passwordRepeat;
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Invalid email")
    private String email;
}
