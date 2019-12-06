package com.example.mnclone.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class PmDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    @NotNull(message="Pm amount cannot be empty")
    private BigDecimal amount;
    @NotNull(message="Ln id cannot be empty")
    private Long lnId;
}
