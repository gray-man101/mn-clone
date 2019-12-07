package com.example.mnclone.dto;

import com.example.mnclone.entity.LnStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Data
public class LnDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    @NotNull(message = "Db name cannot be empty")
    private String dbName;
    @NotNull(message = "Ln amount cannot be empty")
    private BigDecimal amount;
    private LnStatus status;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private ZonedDateTime created;
}
