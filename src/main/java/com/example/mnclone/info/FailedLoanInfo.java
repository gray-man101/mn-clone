package com.example.mnclone.info;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class FailedLoanInfo {
    private String investorEmail;
    private BigDecimal partialRefundAmount;
}
