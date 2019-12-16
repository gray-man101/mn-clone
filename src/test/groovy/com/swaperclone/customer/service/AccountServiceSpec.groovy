package com.swaperclone.customer.service

import com.swaperclone.UnitTestSpec
import com.swaperclone.common.entity.User
import com.swaperclone.common.exception.InsufficientFundsException
import com.swaperclone.common.exception.NotFoundException
import com.swaperclone.common.repository.UserRepository
import com.swaperclone.customer.dto.AccountInfoDTO

class AccountServiceSpec extends UnitTestSpec {

    UserRepository userRepository = Mock(UserRepository)
    AccountService accountService = new AccountService(userRepository: userRepository)

    void setup() {
        userRepository.findRegisteredById(1L) >> Optional.of(prepareUser(id: 1L, email: 'aa@aa.lv', firstName: 'Bb', lastName: 'Aa', balance: BigDecimal.ONE))
        userRepository.findRegisteredById(2L) >> Optional.empty()
    }

    void "test getAccountInfo"() {
        when:
        accountService.getAccountInfo(2L)

        then:
        thrown(NotFoundException)

        when:
        AccountInfoDTO result = accountService.getAccountInfo(1L)

        then:
        result.firstName == 'Bb'
        result.lastName == 'Aa'
        result.email == 'aa@aa.lv'
        result.balance == BigDecimal.ONE
    }

    void "test topUp"() {
        when:
        User user
        accountService.topUp(2L, BigDecimal.valueOf(10))

        then:
        thrown(NotFoundException)

        when:
        accountService.topUp(1L, BigDecimal.valueOf(10))

        then:
        1 * userRepository.save(_) >> { args -> user = args[0] }
        user.getBalance() == BigDecimal.valueOf(11)
    }

    void "test withdraw"() {
        given:
        User user
        userRepository.findRegisteredById(3L) >> Optional.of(prepareUser(id: 3L, email: 'bb@bb.lv', firstName: 'Cc', lastName: 'Dd',
                balance: BigDecimal.valueOf(100)))

        when:
        accountService.withdraw(2L, BigDecimal.valueOf(10))

        then:
        thrown(NotFoundException)

        when:
        accountService.withdraw(1L, BigDecimal.valueOf(10))

        then:
        thrown(InsufficientFundsException)

        when:
        accountService.withdraw(3L, BigDecimal.valueOf(10))

        then:
        1 * userRepository.save(_) >> { args -> user = args[0] }
        user.getBalance() == BigDecimal.valueOf(90)
    }
}
