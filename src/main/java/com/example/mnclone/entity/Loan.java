package com.example.mnclone.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.validator.constraints.Range;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(of = "id")
@ToString(exclude = "payments")
@Entity
public class Loan {

    @Id
    @GeneratedValue(generator = "loan_seq")
    private Long id;
    @Column(nullable = false)
    private String debtorName;
    @Column(nullable = false)
    private BigDecimal amount;
    @Column(nullable = false)
    @Range(min = 1L, max = 20L)
    private BigDecimal investorInterest;
    @Column(nullable = false)
    private BigDecimal amountToReturn;
    @Column(nullable = false)
    private LoanStatus status;
    @Column(nullable = false)
    private ZonedDateTime created;
    @OneToMany(mappedBy = "loan")
    private List<Payment> payments;

}
