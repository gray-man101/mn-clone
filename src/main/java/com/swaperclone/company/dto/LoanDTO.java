package com.swaperclone.company.dto;

import com.swaperclone.common.entity.LoanStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Data
public class LoanDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    @NotBlank(message = "Debtor name cannot be blank")
    private String debtorName;
    @NotNull(message = "Loan amount cannot be empty")
    @Min(value = 100, message = "Loan amount must be at least 100")
    private BigDecimal amount;
    @NotNull(message = "Loan amount to return cannot be empty")
    @Min(value = 100, message = "Loan return amount must be at least 100")
    private BigDecimal amountToReturn;
    @NotNull(message = "Investor interest cannot be empty")
    @Min(value = 5, message = "Investor interest must be at least 5%")
    private BigDecimal investorInterest;
    private LoanStatus status;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private ZonedDateTime created;
}
