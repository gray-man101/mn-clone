package com.swaperclone.company.model;

import java.math.BigDecimal;

public interface InProgressLoanModel {

    BigDecimal getAmount();

    BigDecimal getAmountToReturn();

    BigDecimal getInvestorInterest();

    Long getInvestorId();

    BigDecimal getPaidAmount();

}
