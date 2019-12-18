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

    GreenMail greenMail = new GreenMail(new ServerSetup(1025, "localhost", ServerSetup.PROTOCOL_SMTP))

    Cookie adminCookie
    Cookie customerCookie

    void setup() {
        greenMail.start()
        RestAssured.port = port
        adminCookie = loginAsAdmin()
        customerCookie = registerCustomerAndLogin(email: 'ab@ab.lv')
    }

    void "admin can create-read-update-delete loans"() {
        when:
        int loanArraySize = RestAssured.given().cookie(adminCookie)
                .when().get('/api/loan')
                .then().statusCode(200).extract()
                .body().jsonPath().getList('content').size()
        createLoanAsAdmin()
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
        performLoanUpdateAsAdmin(loanId, [expectedStatus: 200])

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
        performLoanDeleteAsAdmin(loanId, [expectedStatus: 200])

        then:
        loanArraySize == RestAssured.given().cookie(adminCookie)
                .when().get('/api/loan')
                .then().statusCode(200)
                .extract().body().jsonPath().getList('content').size()

    }

    void "admin can create-read-update-delete payments"() {
        when:
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
        loans[0].status == LoanStatus.IN_PROGRESS
        loans[0].debtorName == 'John'
        loans[0].investorInterest == BigDecimal.valueOf(11)
        0 == RestAssured.given().cookie(adminCookie)
                .when().get('/api/loan/' + loanId + '/payment')
                .then().statusCode(200)
                .extract().jsonPath().getList('content').size()

        when:
        RestAssured.given().cookie(adminCookie).contentType(ContentType.JSON)
                .when().body("""
                    {
                        "amount": 100
                    }
                """).post('/api/loan/' + loanId + '/payment')
                .then().statusCode(200)
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
        RestAssured.given().cookie(adminCookie).contentType(ContentType.JSON)
                .when().body("""
                    {
                        "amount": 110
                    }
                """).put('/api/loan/' + loanId + '/payment/' + paymentId)
                .then().statusCode(200)
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
    }

    void "admin cannot update-delete loan with status other than NEW"() {
        given:
        createLoanAsAdmin([amount: BigDecimal.valueOf(500), amountToReturn: BigDecimal.valueOf(1000)])

        when:
        topUpAsCustomer(BigDecimal.valueOf(500))
        Long loanId = performGetAvailableLoansAsCustomer()

        then:
        performInvestOperationAsCustomer(loanId, [expectedStatus: 200])
        checkBalanceAsCustomer(BigDecimal.ZERO)
        performLoanUpdateAsAdmin(loanId, [expectedStatus: 404])
        performLoanDeleteAsAdmin(loanId, [expectedStatus: 404])
    }

    void "customer can top up and withdraw money"() {
        when:
        checkBalanceAsCustomer(BigDecimal.ZERO)
        topUpAsCustomer(BigDecimal.valueOf(450))
        checkBalanceAsCustomer(BigDecimal.valueOf(450))
        withdrawAsCustomer(BigDecimal.valueOf(300))

        then:
        checkBalanceAsCustomer(BigDecimal.valueOf(150))
    }

    void "customer can invest in loans"() {
        expect:
        0 == RestAssured.given().cookie(customerCookie)
                .when().get('/api/investment')
                .then().statusCode(200).extract()
                .jsonPath().getList('content').size()
        when:
        checkBalanceAsCustomer(BigDecimal.ZERO)
        createLoanAsAdmin([amount: BigDecimal.valueOf(1200), amountToReturn: BigDecimal.valueOf(2400)])
        Long loanId = performGetAvailableLoansAsCustomer()

        then:
        performInvestOperationAsCustomer(loanId, [expectedStatus: 400])

        when:
        topUpAsCustomer(BigDecimal.valueOf(1300))
        performInvestOperationAsCustomer(loanId, [expectedStatus: 200])
        checkBalanceAsCustomer(BigDecimal.valueOf(100))

        then:
        1 == RestAssured.given().cookie(customerCookie)
                .when().get('/api/investment')
                .then().statusCode(200).extract()
                .jsonPath().getList('content').size()
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
        assert messages[0].allRecipients.size() == 1
        assert messages[0].allRecipients[0].toString() == params.email
        Matcher matcher = RegistrationSpec.MAIL_CONTENT_PATTERN.matcher(messages[0].content as String)
        assert matcher.find()

        RestAssured.given().redirects().follow(false)
                .when().get("/api/register?token=" + matcher.group(1))
                .then().statusCode(302)
                .and().header(HttpHeaders.LOCATION, 'http://localhost:8080/')
        return RestAssured.given()
                .contentType(ContentType.URLENC)
                .formParam('username', params.email as String).formParam('password', '123')
                .when().post('/login')
                .then().statusCode(200)
                .extract().detailedCookie("JSESSIONID")
    }

    private void performLoanDeleteAsAdmin(long loanId, Map params) {
        RestAssured.given().cookie(adminCookie).contentType(ContentType.JSON)
                .when().delete('/api/loan/' + loanId)
                .then().statusCode(params.expectedStatus as int)
    }

    private void performLoanUpdateAsAdmin(long loanId, Map params) {
        RestAssured.given().cookie(adminCookie).contentType(ContentType.JSON)
                .when().body("""
                    {
                        "debtorName":"John1",
                        "amount": 501,
                        "investorInterest": 12,
                        "amountToReturn": 1001
                    }
                """).put('/api/loan/' + loanId)
                .then().statusCode(params.expectedStatus as int)
    }

    private void createLoanAsAdmin(Map params) {
        RestAssured.given().cookie(adminCookie).contentType(ContentType.JSON)
                .when().body("""
                    {
                        "debtorName":"John",
                        "amount": ${params.amount},
                        "investorInterest": 11,
                        "amountToReturn": ${params.amountToReturn}
                    }
                """).post('/api/loan')
                .then().statusCode(201)
    }

    private void topUpAsCustomer(BigDecimal amount) {
        float initialBalance = RestAssured.given().cookie(customerCookie)
                .when().get('/api/account')
                .then().statusCode(200)
                .extract().jsonPath().getFloat('balance')
        RestAssured.given().cookie(customerCookie).contentType(ContentType.JSON)
                .when().body("{\"amount\": " + amount.toString() + "}").post('/api/account/topUp')
                .then().statusCode(200)

        checkBalanceAsCustomer(amount.add(BigDecimal.valueOf(initialBalance)))
    }

    private void withdrawAsCustomer(BigDecimal amount) {
        float initialBalance = RestAssured.given().cookie(customerCookie)
                .when().get('/api/account')
                .then().statusCode(200)
                .extract().jsonPath().getFloat('balance')
        RestAssured.given().cookie(customerCookie).contentType(ContentType.JSON)
                .when().body("{\"amount\": " + amount.toString() + "}").post('/api/account/withdraw')
                .then().statusCode(200)

        checkBalanceAsCustomer(BigDecimal.valueOf(initialBalance).subtract(amount))
    }

    private void checkBalanceAsCustomer(BigDecimal expectedBalance) {
        float balance = RestAssured.given().cookie(customerCookie)
                .when().get('/api/account')
                .then().statusCode(200)
                .extract().jsonPath().getFloat('balance')
        assert expectedBalance.longValue() == balance
    }

    private void performInvestOperationAsCustomer(Long loanId, Map params) {
        RestAssured.given().cookie(customerCookie)
                .when().post('/api/investment/' + loanId)
                .then().statusCode(params.expectedStatus as int)
    }

    private Long performGetAvailableLoansAsCustomer() {
        String responseBody = RestAssured.given().cookie(customerCookie)
                .when().get('/api/availableLoan')
                .then().statusCode(200)
                .extract().asString()

        LoanDTO[] loans = mapper.readValue(JsonPath.read(responseBody, 'content').toString(), LoanDTO[].class)

        assert loans.length == 1

        return JsonPath.read(responseBody, 'content[0].id')
    }

}
