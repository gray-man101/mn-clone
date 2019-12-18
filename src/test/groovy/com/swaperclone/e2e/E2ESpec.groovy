package com.swaperclone.e2e

import com.fasterxml.jackson.databind.ObjectMapper
import com.icegreen.greenmail.util.GreenMail
import com.icegreen.greenmail.util.ServerSetup
import com.jayway.jsonpath.JsonPath
import com.jayway.restassured.RestAssured
import com.jayway.restassured.http.ContentType
import com.jayway.restassured.response.Cookie
import com.swaperclone.company.dto.LoanDTO
import com.swaperclone.customer.dto.InvestmentDTO
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpHeaders
import spock.lang.Shared
import spock.lang.Specification

import javax.mail.Message
import java.util.regex.Matcher

abstract class E2ESpec extends Specification {

    static GreenMail greenMail = new GreenMail(new ServerSetup(1025, "localhost", ServerSetup.PROTOCOL_SMTP))

    protected static Cookie adminCookie
    protected static Cookie customerCookie
    protected static Cookie someOtherCustomerCookie

    @LocalServerPort
    int port
    @Shared
    protected ObjectMapper mapper = new ObjectMapper()

    void setupSpec() {
        greenMail.start()
    }

    void setup() {
        RestAssured.port = port
        if (adminCookie == null) adminCookie = loginAsAdmin()
        if (customerCookie == null) customerCookie = registerCustomerAndLogin(email: 'ab@ab.lv')
        if (someOtherCustomerCookie == null) someOtherCustomerCookie = registerCustomerAndLogin(email: 'cd@cd.lv')
    }

    protected Cookie loginAsAdmin() {
        return RestAssured.given()
                .contentType(ContentType.URLENC)
                .formParam('username', 'admin')
                .formParam('password', '123')
                .when().post('/login')
                .then().statusCode(200)
                .extract().detailedCookie("JSESSIONID")
    }

    protected Cookie registerCustomerAndLogin(Map params) {
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

    protected int getLoanCountAsAdmin() {
        RestAssured.given().cookie(adminCookie)
                .when().get('/api/loan')
                .then().statusCode(200).extract()
                .body().jsonPath().getList('content').size()
    }

    protected void performLoanCreateAsAdmin(Map params = [:]) {
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

    protected void performLoanUpdateAsAdmin(long loanId, Map params = [:]) {
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

    protected void performLoanDeleteAsAdmin(long loanId, Map params = [:]) {
        RestAssured.given().cookie(adminCookie).contentType(ContentType.JSON)
                .when().delete('/api/loan/' + loanId)
                .then().statusCode(params.expectedStatus as Integer ?: 200)
    }

    protected void performSetLoanStatusFailedAsAdmin(long loanId) {
        RestAssured.given().cookie(adminCookie)
                .when().post('/api/loan/' + loanId + '/fail')
                .then().statusCode(200)
    }

    protected void performPaymentCreateAsAdmin(long loanId, Map params = [:]) {
        RestAssured.given().cookie(adminCookie).contentType(ContentType.JSON)
                .when().body("""
                    {
                        "amount": ${params.amount ?: BigDecimal.valueOf(100)}
                    }
                """).post('/api/loan/' + loanId + '/payment')
                .then().statusCode(params.expectedStatus as Integer ?: 201)
    }

    protected void performPaymentUpdateAsAdmin(long loanId, long paymentId, Map params = [:]) {
        RestAssured.given().cookie(adminCookie).contentType(ContentType.JSON)
                .when().body("""
                    {
                        "amount": ${params.amount ?: BigDecimal.valueOf(100)}
                    }
                """).put('/api/loan/' + loanId + '/payment/' + paymentId)
                .then().statusCode(params.expectedStatus as Integer ?: 200)
    }

    protected void topUpAsCustomer(Map params = [:]) {
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

    protected void withdrawAsCustomer(Map params = [:]) {
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

    protected void checkBalanceAsCustomer(Map params = [:]) {
        float balance = RestAssured.given().cookie(params.cookie as Cookie ?: customerCookie)
                .when().get('/api/account')
                .then().statusCode(200)
                .extract().jsonPath().getFloat('balance')
        assert (params.expectedBalance ?: BigDecimal.ZERO).floatValue() == balance
    }

    protected void performInvestOperationAsCustomer(Long loanId, Map params = [:]) {
        RestAssured.given().cookie(params.cookie as Cookie ?: customerCookie)
                .when().post('/api/investment/' + loanId)
                .then().statusCode(params.expectedStatus as Integer ?: 200)
    }

    protected Long performGetAvailableLoansAsCustomer(Map params = [:]) {
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

    protected InvestmentDTO[] performGetInvestmentsAsCustomer(Map params = [:]) {
        def expectedCount = params.expectedCount ?: 0
        String responseBody = RestAssured.given().cookie(customerCookie)
                .when().get('/api/investment')
                .then().statusCode(200)
                .extract().asString()
        InvestmentDTO[] result = mapper.readValue(JsonPath.read(responseBody, 'content').toString(), InvestmentDTO[].class)
        assert expectedCount == result.length
        return result
    }

    protected void finalizeLoans(List<Long> loanIds) {
        loanIds.each {
            performSetLoanStatusFailedAsAdmin(it)
        }
        withdrawAsCustomer(cookie: customerCookie)
        withdrawAsCustomer(cookie: someOtherCustomerCookie)
    }

}
