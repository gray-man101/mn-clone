package com.swaperclone.common.repository

import com.swaperclone.common.RepositorySpec
import com.swaperclone.common.entity.User

class UserRepositorySpec extends RepositorySpec {


    void "test findRegisteredById"() {
        given:
        User registered = prepareUser(email: 'aa@aa.lv', balance: BigDecimal.ZERO, firstName: 'Aa', lastName: 'Aa', registered: true)
        User notRegistered = prepareUser(email: 'bb@bb.lv', balance: BigDecimal.ZERO, firstName: 'Bb', lastName: 'Bb', registered: false)

        when:
        Optional<User> result1 = userRepository.findRegisteredById(registered.id)
        Optional<User> result2 = userRepository.findRegisteredById(notRegistered.id)

        then:
        result1.get() == registered
        result2.empty
    }

    void "test findRegisteredByEmail"() {
        given:
        User registered = prepareUser(email: 'aa@aa.lv', balance: BigDecimal.ZERO, firstName: 'Aa', lastName: 'Aa', registered: true)
        User notRegistered = prepareUser(email: 'bb@bb.lv', balance: BigDecimal.ZERO, firstName: 'Bb', lastName: 'Bb', registered: false)

        when:
        Optional<User> result1 = userRepository.findRegisteredByEmail('aa@aa.lv')
        Optional<User> result2 = userRepository.findRegisteredByEmail('bb@bb.lv')

        then:
        result1.get() == registered
        result2.empty
    }

    void "test markUserAsRegistered"() {
        given:
        User registered = prepareUser(email: 'aa@aa.lv', balance: BigDecimal.ZERO, firstName: 'Aa', lastName: 'Aa', registered: true, registrationToken: '123')
        User notRegistered = prepareUser(email: 'bb@bb.lv', balance: BigDecimal.ZERO, firstName: 'Bb', lastName: 'Bb', registered: false, registrationToken: '456')

        when:
        int result1 = userRepository.markUserAsRegistered('123')
        int result2 = userRepository.markUserAsRegistered('456')
        em.clear()

        then:
        result1 == 0
        result2 == 1
        userRepository.findById(notRegistered.id).get().registered
    }

}
