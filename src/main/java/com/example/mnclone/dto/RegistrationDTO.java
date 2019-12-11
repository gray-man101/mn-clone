package com.example.mnclone.dto;

import lombok.Data;

@Data
public class RegistrationDTO {
    private String firstName;
    private String lastName;
    private String password;
    private String passwordRepeat;
    private String email;
}
