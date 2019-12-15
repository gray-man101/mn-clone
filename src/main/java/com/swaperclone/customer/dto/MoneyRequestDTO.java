package com.swaperclone.customer.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class MoneyRequestDTO {
    @NotNull(message = "Withdrawal amount cannot be empty")
    @Min(value = 1, message = "Amount must be at least 1")
    private BigDecimal amount;
}
