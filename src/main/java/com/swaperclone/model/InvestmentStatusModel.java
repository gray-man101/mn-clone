package com.swaperclone.model;

import java.math.BigDecimal;

public interface InvestmentStatusModel {
    Long getId();

    String getDebtorName();

    BigDecimal getAmount();

    BigDecimal getAmountToReceive();

    BigDecimal getInterest();

    Integer getPayments();

    BigDecimal getPaidAmount();
}
