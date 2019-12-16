package com.swaperclone.customer.service

import com.swaperclone.UnitTestSpec
import com.swaperclone.common.entity.User
import com.swaperclone.common.exception.NotFoundException
import com.swaperclone.common.repository.UserRepository
import com.swaperclone.customer.dto.RegistrationDTO
import org.springframework.security.crypto.password.PasswordEncoder

class UserServiceSpec extends UnitTestSpec {

    UserRepository userRepository = Mock(UserRepository)
    PasswordEncoder passwordEncoder = Mock(PasswordEncoder)
    UserService userService = new UserService(userRepository: userRepository, passwordEncoder: passwordEncoder)

    void "test register"() {
        given:
        User savedUser
        passwordEncoder.encode(_ as String) >> '123'

        when:
        String token = userService.register(new RegistrationDTO(email: 'aa@aa.lv', firstName: 'Aa', lastName: 'Bb',
                password: 'qwerty', passwordRepeat: 'qwerty'))
        UUID.fromString(token)

        then:
        notThrown(Exception)
        1 * userRepository.save(_) >> { args -> savedUser = args[0] }
        savedUser.email == 'aa@aa.lv'
        savedUser.firstName == 'Aa'
        savedUser.lastName == 'Bb'
        savedUser.registrationToken == token
        savedUser.balance == BigDecimal.ZERO
        savedUser.encodedPassword == '123'
        !savedUser.registered
    }

    void "test completeRegistration"() {
        given:
        userRepository.markUserAsRegistered('123') >> 1
        userRepository.markUserAsRegistered('456') >> 0

        when:
        userService.completeRegistration('456')

        then:
        thrown(NotFoundException)

        when:
        userService.completeRegistration('123')

        then:
        notThrown(NotFoundException)
    }

}
