package com.example.mnclone.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(of = "id")
@Entity
public class Investment {

    @Id
    @GeneratedValue(generator = "investment_seq")
    private Long id;
    @Column(nullable = false)
    private BigDecimal amountToReceive;
    @ManyToOne(optional = false)
    private User investor;
    @ManyToOne(optional = false)
    private Loan loan;

}
