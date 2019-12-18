package com.swaperclone.e2e

import com.icegreen.greenmail.util.GreenMail
import com.icegreen.greenmail.util.ServerSetup
import com.jayway.restassured.RestAssured
import com.jayway.restassured.http.ContentType
import com.jayway.restassured.response.Response
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpHeaders
import spock.lang.Specification

import javax.mail.Message
import java.util.regex.Matcher
import java.util.regex.Pattern

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RegistrationSpec extends Specification {

    @LocalServerPort
    int port

    static GreenMail greenMail = new GreenMail(new ServerSetup(1025, "localhost", ServerSetup.PROTOCOL_SMTP))

    public static final String REGISTRATION_REQUEST_BIDY = """
        {
            "firstName": "John",
            "lastName": "Doe",
            "password": "12345",
            "passwordRepeat": "12345",
            "email": "aa@aa.lv"
        }
    """

    public static final Pattern MAIL_CONTENT_PATTERN = Pattern.compile("""Please complete registration <a href='http://localhost:8080/api/register\\?token=(.*?)'>complete registration</a>\\.""")

    void setup() {
        RestAssured.port = port
        greenMail.start()
    }

    def "user can register and login"() {
        when:
        RestAssured.given().get('/api/account')
                .then().statusCode(401)
        RestAssured.given().get('/api/role')
                .then().statusCode(401)
        RestAssured.given().contentType(ContentType.JSON).body(REGISTRATION_REQUEST_BIDY)
                .when().post("/api/register")
                .then().statusCode(200)

        then:
        greenMail.waitForIncomingEmail(3000, 1)
        Message[] messages = greenMail.getReceivedMessages()
        messages[0].allRecipients.size() == 1
        messages[0].allRecipients[0].toString() == 'aa@aa.lv'
        messages[0].content != null

        when:
        Matcher matcher = MAIL_CONTENT_PATTERN.matcher(messages[0].content as String)

        then:
        matcher.find()

        when:
        RestAssured.given().redirects().follow(false)
                .when().get("/api/register?token=" + matcher.group(1))
                .then().statusCode(302)
                .and().header(HttpHeaders.LOCATION, 'http://localhost:8080/')

        Response response = RestAssured.given()
                .contentType(ContentType.URLENC).formParam('username', 'aa@aa.lv').formParam('password', '123')
                .when().post('/login').thenReturn()
        then:
        response.statusCode() == 200
        RestAssured.given().cookie(response.getDetailedCookie("JSESSIONID"))
                .get('/api/role')
                .then().statusCode(200)
        RestAssured.given().cookie(response.getDetailedCookie("JSESSIONID"))
                .get('/api/account')
                .then().statusCode(200)
    }

    void cleanupSpec() {
        greenMail.stop()
    }

}
