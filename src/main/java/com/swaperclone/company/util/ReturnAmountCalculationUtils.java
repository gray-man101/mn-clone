package com.swaperclone.company.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ReturnAmountCalculationUtils {

    public static BigDecimal calculateInvestorReturnAmount(BigDecimal amount, BigDecimal interest) {
        BigDecimal coefficient = BigDecimal.ONE.add(interest.divide(BigDecimal.valueOf(100), 2, RoundingMode.DOWN));
        return amount.multiply(coefficient);
    }

    public static BigDecimal calculatePartialRefundAmount(BigDecimal paidAmount, BigDecimal amount,
                                                          BigDecimal amountToReturn, BigDecimal interest) {
        BigDecimal coefficient = amount.multiply(BigDecimal.ONE.add(interest.divide(BigDecimal.valueOf(100), 2, RoundingMode.DOWN)))
                .divide(amountToReturn, 4, RoundingMode.DOWN);
        return paidAmount.multiply(coefficient);
    }

}
