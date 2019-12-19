package com.swaperclone.customer.model;

import java.math.BigDecimal;

public interface InvestmentStatusModel {
    Long getId();

    String getDebtorName();

    BigDecimal getAmount();

    BigDecimal getAmountToReceive();

    Integer getPayments();

    BigDecimal getPercentageComplete();
}
