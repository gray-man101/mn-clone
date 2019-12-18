package com.swaperclone.e2e

import com.fasterxml.jackson.databind.ObjectMapper
import com.icegreen.greenmail.util.GreenMail
import com.icegreen.greenmail.util.ServerSetup
import com.jayway.jsonpath.JsonPath
import com.jayway.restassured.RestAssured
import com.jayway.restassured.http.ContentType
import com.jayway.restassured.response.Cookie
import com.swaperclone.common.entity.LoanStatus
import com.swaperclone.company.dto.LoanDTO
import com.swaperclone.company.dto.PaymentDTO
import com.swaperclone.company.util.ReturnAmountCalculationUtils
import com.swaperclone.customer.dto.InvestmentDTO
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpHeaders
import spock.lang.Shared
import spock.lang.Specification

import javax.mail.Message
import java.util.regex.Matcher

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LoanCreationSpec extends Specification {

    @LocalServerPort
    int port

    @Shared
    ObjectMapper mapper = new ObjectMapper()

    static GreenMail greenMail = new GreenMail(new ServerSetup(1025, "localhost", ServerSetup.PROTOCOL_SMTP))

    @Shared
    Cookie adminCookie
    @Shared
    Cookie customerCookie
    @Shared
    Cookie someOtherCustomerCookie

    void setupSpec() {
        greenMail.start()
    }

    void setup() {
        RestAssured.port = port
        if (adminCookie == null) adminCookie = loginAsAdmin()
        if (customerCookie == null) customerCookie = registerCustomerAndLogin(email: 'ab@ab.lv')
        if (someOtherCustomerCookie == null) someOtherCustomerCookie = registerCustomerAndLogin(email: 'cd@cd.lv')
    }

    void "admin can create-read-update-delete loans"() {
        when:
        int loanArraySize = RestAssured.given().cookie(adminCookie)
                .when().get('/api/loan')
                .then().statusCode(200).extract()
                .body().jsonPath().getList('content').size()
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
        loanArraySize == RestAssured.given().cookie(adminCookie)
                .when().get('/api/loan')
                .then().statusCode(200)
                .extract().body().jsonPath().getList('content').size()

    }

    void "admin can create-read-update-delete payments"() {
        when:
        performLoanCreateAsAdmin(amount: BigDecimal.valueOf(1500), amountToReturn: BigDecimal.valueOf(3000))
        String result = RestAssured.given().cookie(adminCookie)
                .when().get('/api/loan')
                .then().statusCode(200).extract()
                .body().asString()
        Long loanId = JsonPath.read(result, 'content[0].id')
        LoanDTO[] loans = mapper.readValue(JsonPath.read(result, 'content').toString(), LoanDTO[].class)

        then:
        loans.size() == 1
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
        performPaymentCreateAsAdmin(loanId)
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

        cleanup:
        finalizeLoans([loanId, otherLoanId])
    }

    void "customer receives profit for completed loan"() {
        when:
        int loanArraySize = RestAssured.given().cookie(adminCookie)
                .when().get('/api/loan')
                .then().statusCode(200).extract()
                .body().jsonPath().getList('content').size()
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
        int loanArraySize = RestAssured.given().cookie(adminCookie)
                .when().get('/api/loan')
                .then().statusCode(200).extract()
                .body().jsonPath().getList('content').size()
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
        performSetLoanStatusFailedAsAdmin(loanId)
        String loanResponseBody = RestAssured.given().cookie(adminCookie)
                .when().get('/api/loan')
                .then().statusCode(200).extract().asString()
        LoanDTO[] loans = mapper.readValue(JsonPath.read(loanResponseBody, 'content').toString(), LoanDTO[].class)

        then:
        loans.length > 0
        loans[0].status == LoanStatus.FAILED
        checkBalanceAsCustomer(expectedBalance: ReturnAmountCalculationUtils.calculatePartialRefundAmount(
                BigDecimal.valueOf(300), BigDecimal.valueOf(1700), BigDecimal.valueOf(3400), BigDecimal.valueOf(11)
        ))

        cleanup:
        withdrawAsCustomer()
    }

    private Cookie loginAsAdmin() {
        return RestAssured.given()
                .contentType(ContentType.URLENC)
                .formParam('username', 'admin')
                .formParam('password', '123')
                .when().post('/login')
                .then().statusCode(200)
                .extract().detailedCookie("JSESSIONID")
    }

    private Cookie registerCustomerAndLogin(Map params) {
        RestAssured.given().contentType(ContentType.JSON)
                .body("""
                    {
                        "firstName": "John",
                        "lastName": "Doe",
                        "password": "12345",
                        "passwordRepeat": "12345",
                        "email": "${params.email}"
                    }
                """)
                .when().post("/api/register")
                .then().statusCode(200)
        greenMail.waitForIncomingEmail(3000, 1)
        Message[] messages = greenMail.getReceivedMessages()
        assert messages.length == 1
        assert messages[0].allRecipients.size() == 1
        assert messages[0].allRecipients[0].toString() == params.email
        Matcher matcher = RegistrationSpec.MAIL_CONTENT_PATTERN.matcher(messages[0].content as String)
        assert matcher.find()

        RestAssured.given().redirects().follow(false)
                .when().get("/api/register?token=" + matcher.group(1))
                .then().statusCode(302)
                .and().header(HttpHeaders.LOCATION, 'http://localhost:8080/')
        greenMail.reset()
        return RestAssured.given()
                .contentType(ContentType.URLENC)
                .formParam('username', params.email as String).formParam('password', '123')
                .when().post('/login')
                .then().statusCode(200)
                .extract().detailedCookie("JSESSIONID")
    }

    private void performLoanCreateAsAdmin(Map params = [:]) {
        RestAssured.given().cookie(adminCookie).contentType(ContentType.JSON)
                .when().body("""
                    {
                        "debtorName":"${params.debtorName ?: 'John'}",
                        "amount": ${params.amount ?: BigDecimal.valueOf(1000)},
                        "investorInterest": 11,
                        "amountToReturn": ${params.amountToReturn}
                    }
                """).post('/api/loan')
                .then().statusCode(params.expectedStatus as Integer ?: 201)
    }

    private void performLoanUpdateAsAdmin(long loanId, Map params = [:]) {
        RestAssured.given().cookie(adminCookie).contentType(ContentType.JSON)
                .when().body("""
                    {
                        "debtorName":"John1",
                        "amount": 501,
                        "investorInterest": 12,
                        "amountToReturn": 1001
                    }
                """).put('/api/loan/' + loanId)
                .then().statusCode(params.expectedStatus as Integer ?: 200)
    }

    private void performLoanDeleteAsAdmin(long loanId, Map params = [:]) {
        RestAssured.given().cookie(adminCookie).contentType(ContentType.JSON)
                .when().delete('/api/loan/' + loanId)
                .then().statusCode(params.expectedStatus as Integer ?: 200)
    }

    private void performSetLoanStatusFailedAsAdmin(long loanId) {
        RestAssured.given().cookie(adminCookie)
                .when().post('/api/loan/' + loanId + '/fail')
                .then().statusCode(200)
    }

    private void performPaymentCreateAsAdmin(long loanId, Map params = [:]) {
        RestAssured.given().cookie(adminCookie).contentType(ContentType.JSON)
                .when().body("""
                    {
                        "amount": ${params.amount ?: BigDecimal.valueOf(100)}
                    }
                """).post('/api/loan/' + loanId + '/payment')
                .then().statusCode(params.expectedStatus as Integer ?: 201)
    }

    private void performPaymentUpdateAsAdmin(long loanId, long paymentId, Map params = [:]) {
        RestAssured.given().cookie(adminCookie).contentType(ContentType.JSON)
                .when().body("""
                    {
                        "amount": ${params.amount ?: BigDecimal.valueOf(100)}
                    }
                """).put('/api/loan/' + loanId + '/payment/' + paymentId)
                .then().statusCode(params.expectedStatus as Integer ?: 200)
    }

    private void topUpAsCustomer(Map params = [:]) {
        Cookie cookie = params.cookie as Cookie ?: customerCookie
        float initialBalance = RestAssured.given().cookie(cookie)
                .when().get('/api/account')
                .then().statusCode(200)
                .extract().jsonPath().getFloat('balance')
        RestAssured.given().cookie(cookie).contentType(ContentType.JSON)
                .when().body("{\"amount\": " + params.amount.toString() + "}").post('/api/account/topUp')
                .then().statusCode(200)

        checkBalanceAsCustomer(cookie: cookie, expectedBalance: params.amount.add(BigDecimal.valueOf(initialBalance)))
    }

    private void withdrawAsCustomer(Map params = [:]) {
        Cookie cookie = params.cookie as Cookie ?: customerCookie
        float initialBalance = RestAssured.given().cookie(cookie)
                .when().get('/api/account')
                .then().statusCode(200)
                .extract().jsonPath().getFloat('balance')
        BigDecimal amount = params.amount as BigDecimal ?: initialBalance
        if (BigDecimal.ZERO == amount) {
            return
        }
        RestAssured.given().cookie(cookie).contentType(ContentType.JSON)
                .when().body("{\"amount\": " + amount.toString() + "}").post('/api/account/withdraw')
                .then().statusCode(200)

        checkBalanceAsCustomer(cookie: cookie, expectedBalance: BigDecimal.valueOf(initialBalance).subtract(amount))
    }

    private void checkBalanceAsCustomer(Map params = [:]) {
        float balance = RestAssured.given().cookie(params.cookie as Cookie ?: customerCookie)
                .when().get('/api/account')
                .then().statusCode(200)
                .extract().jsonPath().getFloat('balance')
        assert (params.expectedBalance ?: BigDecimal.ZERO).floatValue() == balance
    }

    private void performInvestOperationAsCustomer(Long loanId, Map params = [:]) {
        RestAssured.given().cookie(params.cookie as Cookie ?: customerCookie)
                .when().post('/api/investment/' + loanId)
                .then().statusCode(params.expectedStatus as Integer ?: 200)
    }

    private Long performGetAvailableLoansAsCustomer(Map params = [:]) {
        String responseBody = RestAssured.given().cookie(params.cookie as Cookie ?: customerCookie)
                .when().get('/api/availableLoan')
                .then().statusCode(200)
                .extract().asString()

        LoanDTO[] loans = mapper.readValue(JsonPath.read(responseBody, 'content').toString(), LoanDTO[].class)

        int expectedCount = params.expectedAvailableLoanCount != null ? params.expectedAvailableLoanCount as int : 1
        assert loans.length == expectedCount
        if (expectedCount != 1) {
            return null
        }

        return JsonPath.read(responseBody, 'content[0].id')
    }

    private InvestmentDTO[] performGetInvestmentsAsCustomer(Map params = [:]) {
        def expectedCount = params.expectedCount ?: 0
        String responseBody = RestAssured.given().cookie(customerCookie)
                .when().get('/api/investment')
                .then().statusCode(200)
                .extract().asString()
        InvestmentDTO[] result = mapper.readValue(JsonPath.read(responseBody, 'content').toString(), InvestmentDTO[].class)
        assert expectedCount == result.length
        return result
    }

    private void finalizeLoans(List<Long> loanIds) {
        loanIds.each {
            performSetLoanStatusFailedAsAdmin(it)
        }
        withdrawAsCustomer(cookie: customerCookie)
        withdrawAsCustomer(cookie: someOtherCustomerCookie)
    }

}
