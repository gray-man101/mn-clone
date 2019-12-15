package com.swaperclone.common.repository


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@SpringBootTest
class InvestmentRepositorySpec extends Specification {

    @Autowired
    InvestmentRepository investmentRepository

    def "test bbb"() {
        given:
        println investmentRepository.hashCode()

        when:
        println 'context loads'

        then:
        1 == 2
    }

}
