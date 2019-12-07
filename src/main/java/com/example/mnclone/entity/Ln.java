package com.example.mnclone.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Data
@EqualsAndHashCode
@Entity
public class Ln {

    @Id
    @GeneratedValue(generator = "ln_seq")
    private Long id;
    private String dbName;
    private BigDecimal amount;
    private LnStatus status;
    private ZonedDateTime created;

}
