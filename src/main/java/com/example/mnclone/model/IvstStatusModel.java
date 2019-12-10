package com.example.mnclone.model;

import java.math.BigDecimal;

public interface IvstStatusModel {
    Long getId();

    BigDecimal getOverallAmount();

    Integer getPayments();

    BigDecimal getPaidAmount();
}
