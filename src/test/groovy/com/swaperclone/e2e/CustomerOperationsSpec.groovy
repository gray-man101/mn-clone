package com.swaperclone.e2e

import com.jayway.jsonpath.JsonPath
import com.jayway.restassured.RestAssured
import com.swaperclone.common.entity.LoanStatus
import com.swaperclone.company.dto.LoanDTO
import com.swaperclone.company.util.ReturnAmountCalculationUtils
import com.swaperclone.customer.dto.InvestmentDTO
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CustomerOperationsSpec extends E2ESpec {

    void "customer can top up and withdraw money"() {
        when:
        checkBalanceAsCustomer(expectedBalance: BigDecimal.ZERO)
        topUpAsCustomer(amount: BigDecimal.valueOf(450))
        checkBalanceAsCustomer(expectedBalance: BigDecimal.valueOf(450))
        withdrawAsCustomer(amount: BigDecimal.valueOf(300))

        then:
        checkBalanceAsCustomer(expectedBalance: BigDecimal.valueOf(150))

        cleanup:
        withdrawAsCustomer()
    }

    void "customer can invest in loans"() {
        when:
        performGetInvestmentsAsCustomer(expectedCount: 0)
        checkBalanceAsCustomer(expectedBalance: BigDecimal.ZERO)
        performLoanCreateAsAdmin(amount: BigDecimal.valueOf(1200), amountToReturn: BigDecimal.valueOf(2400), debtorName: 'Steve')
        Long loanId = performGetAvailableLoansAsCustomer(cookie: customerCookie, expectedAvailableLoanCount: 1)
        performGetAvailableLoansAsCustomer(cookie: someOtherCustomerCookie, expectedAvailableLoanCount: 1)

        then:
        performInvestOperationAsCustomer(loanId, [cookie: customerCookie, expectedStatus: 400])

        when:
        topUpAsCustomer(amount: BigDecimal.valueOf(1300))
        performInvestOperationAsCustomer(loanId, [cookie: customerCookie, expectedStatus: 200])

        then:
        checkBalanceAsCustomer(expectedBalance: BigDecimal.valueOf(100))
        null == performGetAvailableLoansAsCustomer(cookie: customerCookie, expectedAvailableLoanCount: 0)
        null == performGetAvailableLoansAsCustomer(cookie: someOtherCustomerCookie, expectedAvailableLoanCount: 0)

        when:
        performLoanCreateAsAdmin(amount: BigDecimal.valueOf(1600), amountToReturn: BigDecimal.valueOf(3200), debtorName: 'Peter')
        topUpAsCustomer(cookie: someOtherCustomerCookie, amount: BigDecimal.valueOf(1600))
        Long otherLoanId = performGetAvailableLoansAsCustomer(cookie: customerCookie, expectedAvailableLoanCount: 1)
        performGetAvailableLoansAsCustomer(cookie: someOtherCustomerCookie, expectedAvailableLoanCount: 1)
        performInvestOperationAsCustomer(otherLoanId, [cookie: someOtherCustomerCookie])

        then:
        null == performGetAvailableLoansAsCustomer(cookie: customerCookie, expectedAvailableLoanCount: 0)
        null == performGetAvailableLoansAsCustomer(cookie: someOtherCustomerCookie, expectedAvailableLoanCount: 0)
        performInvestOperationAsCustomer(loanId, [expectedStatus: 404])
        InvestmentDTO[] investmentDTOS = performGetInvestmentsAsCustomer(expectedCount: 1)
        investmentDTOS[0].debtorName == 'Steve'
        investmentDTOS[0].payments == 0
        investmentDTOS[0].percentageComplete == BigDecimal.ZERO
        investmentDTOS[0].overallAmount == BigDecimal.valueOf(1200)
        investmentDTOS[0].amountToReceive == BigDecimal.valueOf(1332)

        when:
        performPaymentCreateAsAdmin(loanId, [amount: BigDecimal.valueOf(200)])
        performPaymentCreateAsAdmin(loanId, [amount: BigDecimal.valueOf(200)])
        investmentDTOS = performGetInvestmentsAsCustomer(expectedCount: 1)

        then:
        investmentDTOS[0].debtorName == 'Steve'
        investmentDTOS[0].payments == 2
        investmentDTOS[0].percentageComplete == BigDecimal.valueOf(16.66)
        investmentDTOS[0].overallAmount == BigDecimal.valueOf(1200)
        investmentDTOS[0].amountToReceive == BigDecimal.valueOf(1332)

        cleanup:
        finalizeLoans([loanId, otherLoanId])
    }

    void "customer receives profit for completed loan"() {
        when:
        int loanArraySize = getLoanCountAsAdmin()
        checkBalanceAsCustomer(expectedBalance: BigDecimal.ZERO)
        topUpAsCustomer(amount: BigDecimal.valueOf(2000))
        performLoanCreateAsAdmin(amount: BigDecimal.valueOf(2000), amountToReturn: BigDecimal.valueOf(4000))
        String loanResponseBody = RestAssured.given().cookie(adminCookie)
                .when().get('/api/loan')
                .then().statusCode(200).extract().asString()
        LoanDTO[] loans = mapper.readValue(JsonPath.read(loanResponseBody, 'content').toString(), LoanDTO[].class)

        then:
        loans.length == loanArraySize + 1
        loans[0].status == LoanStatus.NEW
        loans[0].amount == BigDecimal.valueOf(2000)
        loans[0].amountToReturn == BigDecimal.valueOf(4000)

        when:
        Long loanId = performGetAvailableLoansAsCustomer(expectedAvailableLoanCount: 1)
        performInvestOperationAsCustomer(loanId)
        loanResponseBody = RestAssured.given().cookie(adminCookie)
                .when().get('/api/loan')
                .then().statusCode(200).extract().asString()
        loans = mapper.readValue(JsonPath.read(loanResponseBody, 'content').toString(), LoanDTO[].class)

        then:
        loans.length == loanArraySize + 1
        loans[0].status == LoanStatus.IN_PROGRESS

        when:
        performPaymentCreateAsAdmin(loanId, [amount: BigDecimal.valueOf(2000)])
        loanResponseBody = RestAssured.given().cookie(adminCookie)
                .when().get('/api/loan')
                .then().statusCode(200).extract().asString()
        loans = mapper.readValue(JsonPath.read(loanResponseBody, 'content').toString(), LoanDTO[].class)

        then:
        loans.length == loanArraySize + 1
        loans[0].status == LoanStatus.IN_PROGRESS

        when:
        performPaymentCreateAsAdmin(loanId, [amount: BigDecimal.valueOf(2000)])
        loanResponseBody = RestAssured.given().cookie(adminCookie)
                .when().get('/api/loan')
                .then().statusCode(200).extract().asString()
        loans = mapper.readValue(JsonPath.read(loanResponseBody, 'content').toString(), LoanDTO[].class)

        then:
        loans.length == loanArraySize + 1
        loans[0].status == LoanStatus.COMPLETE
        checkBalanceAsCustomer(expectedBalance: BigDecimal.valueOf(2220))

        cleanup:
        withdrawAsCustomer()
    }

    void "customer receives partial refund if loan status becomes FAILED"() {
        when:
        int loanArraySize = getLoanCountAsAdmin()
        checkBalanceAsCustomer(expectedBalance: BigDecimal.ZERO)
        topUpAsCustomer(amount: BigDecimal.valueOf(1700))
        performLoanCreateAsAdmin(amount: BigDecimal.valueOf(1700), amountToReturn: BigDecimal.valueOf(3400))
        Long loanId = performGetAvailableLoansAsCustomer(expectedAvailableLoanCount: 1)
        performInvestOperationAsCustomer(loanId)

        then:
        null == performGetAvailableLoansAsCustomer(expectedAvailableLoanCount: 0)
        checkBalanceAsCustomer(expectedBalance: BigDecimal.ZERO)

        when:
        performPaymentCreateAsAdmin(loanId, [amount: BigDecimal.valueOf(100)])
        performPaymentCreateAsAdmin(loanId, [amount: BigDecimal.valueOf(100)])
        performPaymentCreateAsAdmin(loanId, [amount: BigDecimal.valueOf(100)])

        then:
        InvestmentDTO[] investmentDTOS = performGetInvestmentsAsCustomer(expectedCount: 1)
        investmentDTOS[0].debtorName == 'John'
        investmentDTOS[0].payments == 3
        investmentDTOS[0].percentageComplete == BigDecimal.valueOf(8.82)
        investmentDTOS[0].overallAmount == BigDecimal.valueOf(1700)
        investmentDTOS[0].amountToReceive == BigDecimal.valueOf(1887)

        when:
        performSetLoanStatusFailedAsAdmin(loanId)
        String loanResponseBody = RestAssured.given().cookie(adminCookie)
                .when().get('/api/loan')
                .then().statusCode(200).extract().asString()
        LoanDTO[] loans = mapper.readValue(JsonPath.read(loanResponseBody, 'content').toString(), LoanDTO[].class)

        then:
        loans.length == loanArraySize + 1
        loans[0].status == LoanStatus.FAILED
        checkBalanceAsCustomer(expectedBalance: ReturnAmountCalculationUtils.calculatePartialRefundAmount(
                BigDecimal.valueOf(300), BigDecimal.valueOf(1700), BigDecimal.valueOf(3400), BigDecimal.valueOf(11)
        ))

        cleanup:
        withdrawAsCustomer()
    }


}
