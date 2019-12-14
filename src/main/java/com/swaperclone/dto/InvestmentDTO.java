package com.swaperclone.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class InvestmentDTO {
    private Long id;
    private String debtorName;
    private Integer payments;
    private BigDecimal paidAmount;
    private BigDecimal overallAmount;
    private BigDecimal amountToReceive;
}
