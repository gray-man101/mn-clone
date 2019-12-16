package com.swaperclone.e2e

import com.fasterxml.jackson.databind.ObjectMapper
import com.jayway.jsonpath.JsonPath
import com.jayway.restassured.RestAssured
import com.jayway.restassured.http.ContentType
import com.jayway.restassured.response.Cookie
import com.swaperclone.common.entity.LoanStatus
import com.swaperclone.company.dto.LoanDTO
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import spock.lang.Shared
import spock.lang.Specification

import static org.hamcrest.Matchers.hasSize

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LoanCreationSpec extends Specification {

    @LocalServerPort
    int port

    @Shared
    ObjectMapper mapper = new ObjectMapper()

    void setup() {
        RestAssured.port = port
        Cookie adminCookie = RestAssured.given()
                .contentType(ContentType.URLENC)
                .formParam('username', 'admin')
                .formParam('password', '123')
                .when().post('/login')
                .then().statusCode(200)
                .extract().detailedCookie("JSESSIONID")
        RestAssured.requestSpecification = RestAssured.given().cookie(adminCookie)
    }

    void "admin can create loans"() {
        when:
        RestAssured.when().get('/api/loan')
                .then().statusCode(200).body('content', hasSize(0))
        RestAssured.given().contentType(ContentType.JSON)
                .when().body("""
                    {
                        "debtorName":"John",
                        "amount": 500,
                        "investorInterest": 11,
                        "amountToReturn": 1000
                    }
                """).post('/api/loan')
                .then().statusCode(201)
        String result = RestAssured.when().get('/api/loan')
                .then().statusCode(200)
                .extract().body().asString()
        LoanDTO[] loans = mapper.readValue(JsonPath.read(result, 'content').toString(), LoanDTO[].class)

        then:
        loans.size() == 1
        loans[0].debtorName == 'John'
        loans[0].amount == BigDecimal.valueOf(500)
        loans[0].amountToReturn == BigDecimal.valueOf(1000)
        loans[0].investorInterest == BigDecimal.valueOf(11)
        loans[0].status == LoanStatus.NEW
    }

}
