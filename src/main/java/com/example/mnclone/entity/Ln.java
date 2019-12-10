package com.example.mnclone.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
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
    private String dbName;
    private BigDecimal amount;
    private LnStatus status;
    private ZonedDateTime created;
    @OneToMany(mappedBy = "ln")
    private List<Pm> ps;

}
