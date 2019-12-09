package com.example.mnclone.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountInfoDTO {
    private String firstName;
    private String lastName;
    private String email;
    private BigDecimal balance;
//    private String role = "COMPANY_ADMIN";
    private String role = "CUSTOMER";
}
