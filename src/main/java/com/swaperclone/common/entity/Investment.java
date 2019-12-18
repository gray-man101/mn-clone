package com.swaperclone.common.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(of = "id")
@Entity
public class Investment {

    @Id
    @SequenceGenerator(name = "investment_seq", sequenceName = "investment_seq", allocationSize = 1)
    @GeneratedValue(generator = "investment_seq", strategy = GenerationType.SEQUENCE)
    private Long id;
    @Column(nullable = false)
    private BigDecimal amountToReceive;
    @ManyToOne(optional = false)
    private User investor;
    @OneToOne(optional = false)
    private Loan loan;

}
