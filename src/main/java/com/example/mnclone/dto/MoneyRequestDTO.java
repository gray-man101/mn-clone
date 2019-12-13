package com.example.mnclone.dto;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class MoneyRequestDTO {
    //TODO test annotations
    @DecimalMin(value = "0.0", inclusive = false)
    @NotNull(message = "Withdrawal amount cannot be empty")
    private BigDecimal amount;
}
