package com.example.mnclone.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class IvstStatusModel {
    private Long id;
    private BigDecimal overallAmount;
    private Integer payments;
    private BigDecimal paidAmount;
}
