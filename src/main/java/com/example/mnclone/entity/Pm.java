package com.example.mnclone.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Data
@EqualsAndHashCode(of = "id")
@Entity
public class Pm {

    @Id
    @GeneratedValue(generator = "pm_seq")
    private Long id;
    private BigDecimal amount;
    private ZonedDateTime created;
    @ManyToOne
    @JoinColumn(name = "ln_id")
    private Ln ln;

}
