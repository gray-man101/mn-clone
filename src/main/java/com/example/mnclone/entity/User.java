package com.example.mnclone.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
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
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String passwordHash;
    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String lastName;
    @Column(nullable = false)
    private BigDecimal balance;
    @Column(nullable = false)
    private Boolean registered;
    @Column(nullable = false)
    private String registrationToken;

}
