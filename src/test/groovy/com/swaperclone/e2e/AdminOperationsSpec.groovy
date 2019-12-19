package com.swaperclone.e2e

import com.jayway.jsonpath.JsonPath
import com.jayway.restassured.RestAssured
import com.jayway.restassured.http.ContentType
import com.swaperclone.common.entity.LoanStatus
import com.swaperclone.company.dto.LoanDTO
import com.swaperclone.company.dto.PaymentDTO
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AdminOperationsSpec extends E2ESpec {

    void "admin can create-read-update-delete loans"() {
        when:
        int loanArraySize = getLoanCountAsAdmin()
        performLoanCreateAsAdmin(amount: BigDecimal.valueOf(500), amountToReturn: BigDecimal.valueOf(1000))
        String result = RestAssured.given().cookie(adminCookie)
                .when().get('/api/loan')
                .then().statusCode(200)
                .extract().body().asString()
        Long loanId = JsonPath.read(result, 'content[0].id')
        LoanDTO[] loans = mapper.readValue(JsonPath.read(result, 'content').toString(), LoanDTO[].class)

        then:
        loans.size() == loanArraySize + 1
        loans[0].debtorName == 'John'
        loans[0].amount == BigDecimal.valueOf(500)
        loans[0].amountToReturn == BigDecimal.valueOf(1000)
        loans[0].investorInterest == BigDecimal.valueOf(11)
        loans[0].status == LoanStatus.NEW

        when:
        performLoanUpdateAsAdmin(loanId)

        result = RestAssured.given().cookie(adminCookie)
                .when().get('/api/loan')
                .then().statusCode(200)
                .extract().body().asString()
        loans = mapper.readValue(JsonPath.read(result, 'content').toString(), LoanDTO[].class)

        then:
        loans.size() == loanArraySize + 1
        loans[0].debtorName == 'John1'
        loans[0].amount == BigDecimal.valueOf(501)
        loans[0].amountToReturn == BigDecimal.valueOf(1001)
        loans[0].investorInterest == BigDecimal.valueOf(12)
        loans[0].status == LoanStatus.NEW

        when:
        performLoanDeleteAsAdmin(loanId)

        then:
        loanArraySize == getLoanCountAsAdmin()
    }

    void "admin can create-read-update-delete payments"() {
        when:
        int loanArraySize = getLoanCountAsAdmin()
        performLoanCreateAsAdmin(amount: BigDecimal.valueOf(1500), amountToReturn: BigDecimal.valueOf(3000))
        String result = RestAssured.given().cookie(adminCookie)
                .when().get('/api/loan')
                .then().statusCode(200).extract()
                .body().asString()
        Long loanId = JsonPath.read(result, 'content[0].id')
        LoanDTO[] loans = mapper.readValue(JsonPath.read(result, 'content').toString(), LoanDTO[].class)

        then:
        loans.size() == loanArraySize + 1
        loans[0].amount == BigDecimal.valueOf(1500)
        loans[0].amountToReturn == BigDecimal.valueOf(3000)
        loans[0].status == LoanStatus.NEW
        loans[0].debtorName == 'John'
        loans[0].investorInterest == BigDecimal.valueOf(11)
        0 == RestAssured.given().cookie(adminCookie)
                .when().get('/api/loan/' + loanId + '/payment')
                .then().statusCode(200)
                .extract().jsonPath().getList('content').size()

        when:
        topUpAsCustomer(amount: BigDecimal.valueOf(1500))
        performInvestOperationAsCustomer(loanId)
        performPaymentCreateAsAdmin(loanId, [amount: BigDecimal.valueOf(100)])
        result = RestAssured.given().cookie(adminCookie)
                .when().get('/api/loan/' + loanId + '/payment')
                .then().statusCode(200)
                .extract().body().asString()
        Long paymentId = JsonPath.read(result, 'content[0].id')
        PaymentDTO[] payments = mapper.readValue(JsonPath.read(result, 'content').toString(), PaymentDTO[].class)

        then:
        payments.size() == 1
        payments[0].amount == BigDecimal.valueOf(100)

        when:
        performPaymentUpdateAsAdmin(loanId, paymentId, [amount: BigDecimal.valueOf(110)])
        result = RestAssured.given().cookie(adminCookie)
                .when().get('/api/loan/' + loanId + '/payment')
                .then().statusCode(200)
                .extract().body().asString()
        payments = mapper.readValue(JsonPath.read(result, 'content').toString(), PaymentDTO[].class)

        then:
        payments.size() == 1
        payments[0].amount == BigDecimal.valueOf(110)
        performPaymentCreateAsAdmin(loanId, [amount: BigDecimal.valueOf(3500), expectedStatus: 400])

        when:
        RestAssured.given().cookie(adminCookie).contentType(ContentType.JSON)
                .when().delete('/api/loan/' + loanId + '/payment/' + paymentId)
                .then().statusCode(200)

        then:
        0 == RestAssured.given().cookie(adminCookie)
                .when().get('/api/loan/' + loanId + '/payment')
                .then().statusCode(200)
                .extract().body().jsonPath().getList('content').size()
        performLoanDeleteAsAdmin(loanId, [expectedStatus: 404])

        cleanup:
        finalizeLoans([loanId])
    }

    void "admin cannot create loan with insufficient return amount"() {
        expect:
        performLoanCreateAsAdmin(amount: BigDecimal.valueOf(2000), amountToReturn: BigDecimal.valueOf(2000), expectedStatus: 400)
    }

    void "admin cannot update-delete loan with status other than NEW"() {
        given:
        performLoanCreateAsAdmin(amount: BigDecimal.valueOf(500), amountToReturn: BigDecimal.valueOf(1000))

        when:
        topUpAsCustomer(amount: BigDecimal.valueOf(500))
        Long loanId = performGetAvailableLoansAsCustomer(expectedAvailableLoanCount: 1)

        then:
        performInvestOperationAsCustomer(loanId)
        checkBalanceAsCustomer(expectedBalance: BigDecimal.ZERO)
        performLoanUpdateAsAdmin(loanId, [expectedStatus: 404])
        performLoanDeleteAsAdmin(loanId, [expectedStatus: 404])

        cleanup:
        finalizeLoans([loanId])
    }
}
