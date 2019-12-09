package com.example.mnclone.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class IvstStatusDTO {
    private Long id;
    private Integer payments;
    private BigDecimal paidAmount;
    private BigDecimal overallAmount;
}
