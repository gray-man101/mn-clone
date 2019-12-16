package com.swaperclone.e2e

import com.jayway.restassured.RestAssured
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import spock.lang.Specification

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FirstE2ESpec extends Specification {

    @LocalServerPort
    int port

    void setup() {
        RestAssured.port = port
    }

    def "test ccc"() {
        given:
        RestAssured.when().get("/api/register?token=123")
                .then().statusCode(404)
//        mvc.perform(MockMvcRequestBuilders.get("/api/register"))
//                .andExpect(status().isOk())
        when:
        println 'a'

        then:
        1 == 1
    }

}
