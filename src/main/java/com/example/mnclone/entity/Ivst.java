package com.example.mnclone.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Data
@EqualsAndHashCode(of = "id")
@Entity
public class Ivst {

    @Id
    @GeneratedValue(generator = "ivst_seq")
    private Long id;
    @ManyToOne
    private User ivstr;
    @ManyToOne
    private Ln ln;

}
