package com.example.mnclone.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class NewLnDTO {
    private String dbName;
    private BigDecimal amount;
    private String status;
}
