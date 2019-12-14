package com.swaperclone.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Data
public class PaymentDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    @NotNull(message = "Payment amount cannot be empty")
    @Min(value = 10, message = "Amount must be at least 10")
    private BigDecimal amount;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private ZonedDateTime created;
}
