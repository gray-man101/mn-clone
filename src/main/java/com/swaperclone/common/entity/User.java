package com.swaperclone.common.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.Min;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "`user`")
public class User {

    @Id
    @SequenceGenerator(name = "user_seq", sequenceName = "user_seq", allocationSize = 1)
    @GeneratedValue(generator = "user_seq", strategy = GenerationType.SEQUENCE)
    private Long id;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String encodedPassword;
    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String lastName;
    @Column(nullable = false)
    @Min(0)
    private BigDecimal balance;
    @Column(nullable = false)
    private Boolean registered;
    @Column(nullable = false)
    private String registrationToken;

}
