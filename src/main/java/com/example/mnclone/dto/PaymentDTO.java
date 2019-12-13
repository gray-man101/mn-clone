package com.example.mnclone.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Data
public class PaymentDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    @NotNull(message = "Payment amount cannot be empty")
    private BigDecimal amount;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private ZonedDateTime created;
}
