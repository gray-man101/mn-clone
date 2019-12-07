package com.example.mnclone.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(of = "id")
@Entity
public class User {

    @Id
    @GeneratedValue(generator = "user_seq")
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private BigDecimal balance;
    private Boolean registered;
    private String registrationToken;

}
