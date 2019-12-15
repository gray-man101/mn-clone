package com.swaperclone.company.util

import spock.lang.Specification

class ReturnAmountCalculationUtilsSpec extends Specification {

    void "test calculateInvestorReturnAmount"() {
        expect:
        expectedResult == ReturnAmountCalculationUtils.calculateInvestorReturnAmount(amount, interest)

        where:
        amount                  | interest               | expectedResult
        BigDecimal.valueOf(100) | BigDecimal.valueOf(11) | BigDecimal.valueOf(111)
        BigDecimal.valueOf(104) | BigDecimal.valueOf(11) | BigDecimal.valueOf(115.44)
        BigDecimal.valueOf(105) | BigDecimal.valueOf(11) | BigDecimal.valueOf(116.55)
        BigDecimal.valueOf(109) | BigDecimal.valueOf(11) | BigDecimal.valueOf(120.99)
        BigDecimal.valueOf(110) | BigDecimal.valueOf(11) | BigDecimal.valueOf(122.10)
    }

    void "test calculatePartialRefundAmount"() {
        expect:
        expectedResult == ReturnAmountCalculationUtils.calculatePartialRefundAmount(paidAmount, amount, amountToReturn, interest)

        where:
        paidAmount              | amount                   | interest               | amountToReturn           | expectedResult
        BigDecimal.valueOf(200) | BigDecimal.valueOf(1000) | BigDecimal.valueOf(11) | BigDecimal.valueOf(2000) | BigDecimal.valueOf(111)
        BigDecimal.valueOf(200) | BigDecimal.valueOf(1000) | BigDecimal.valueOf(11) | BigDecimal.valueOf(1999) | BigDecimal.valueOf(111.04)
        BigDecimal.valueOf(200) | BigDecimal.valueOf(1000) | BigDecimal.valueOf(11) | BigDecimal.valueOf(1998) | BigDecimal.valueOf(111.10)
    }

}
