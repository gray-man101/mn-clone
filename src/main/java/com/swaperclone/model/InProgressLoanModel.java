package com.swaperclone.model;

import java.math.BigDecimal;

public interface InProgressLoanModel {

    Long getLoanId();

    BigDecimal getAmount();

    BigDecimal getAmountToReturn();

    BigDecimal getInvestorInterest();

    Long getInvestorId();

    BigDecimal getPaidAmount();

}
