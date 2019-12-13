package com.example.mnclone.dto;

import com.example.mnclone.entity.LoanStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Data
public class LoanDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    @NotNull(message = "Db name cannot be empty")
    private String dbName;
    @NotNull(message = "Loan amount cannot be empty")
    private BigDecimal amount;
    @NotNull(message = "Loan amount to return cannot be empty")
    private BigDecimal amountToReturn;
    private LoanStatus status;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private ZonedDateTime created;
}
