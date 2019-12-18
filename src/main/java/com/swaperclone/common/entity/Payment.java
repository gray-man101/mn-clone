package com.swaperclone.common.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.Min;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Data
@EqualsAndHashCode(of = "id")
@Entity
public class Payment {

    @Id
    @SequenceGenerator(name = "payment_seq", sequenceName = "payment_seq", allocationSize = 1)
    @GeneratedValue(generator = "payment_seq", strategy = GenerationType.SEQUENCE)
    private Long id;
    @Column(nullable = false)
    @Min(10L)
    private BigDecimal amount;
    @Column(nullable = false)
    private ZonedDateTime created;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Loan loan;

}
