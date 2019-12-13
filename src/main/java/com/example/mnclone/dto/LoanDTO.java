package com.example.mnclone.dto;

import com.example.mnclone.entity.LoanStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Data
public class LoanDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    @NotEmpty(message = "Debtor name cannot be empty")
    private String debtorName;
    @NotNull(message = "Loan amount cannot be empty")
    private BigDecimal amount;
    @NotNull(message = "Loan amount to return cannot be empty")
    private BigDecimal amountToReturn;
    @NotNull(message = "Investor interest cannot be empty")
    private BigDecimal investorInterest;
    private LoanStatus status;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private ZonedDateTime created;
}
