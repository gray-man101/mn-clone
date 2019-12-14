package com.swaperclone.entity;

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
    @GeneratedValue(generator = "payment_seq")
    private Long id;
    @Column(nullable = false)
    @Min(10L)
    private BigDecimal amount;
    @Column(nullable = false)
    private ZonedDateTime created;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Loan loan;

}
