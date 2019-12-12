package com.example.mnclone.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(of = "id")
@ToString(exclude = "ps")
@Entity
public class Ln {

    @Id
    @GeneratedValue(generator = "ln_seq")
    private Long id;
    @Column(nullable = false)
    private String dbName;
    @Column(nullable = false)
    private BigDecimal amount;
    @Column(nullable = false)
    private BigDecimal amountToReturn;
    @Column(nullable = false)
    private LnStatus status;
    @Column(nullable = false)
    private ZonedDateTime created;
    @OneToMany(mappedBy = "ln")
    private List<Pm> ps;

}
