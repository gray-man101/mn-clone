package com.example.mnclone.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PmDTO {
    private BigDecimal amount;
    private Long lnId;
}
