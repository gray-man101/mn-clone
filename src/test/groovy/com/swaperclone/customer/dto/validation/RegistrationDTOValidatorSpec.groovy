package com.swaperclone.customer.dto.validation

import com.swaperclone.customer.dto.RegistrationDTO
import org.springframework.validation.Errors
import spock.lang.Specification

class RegistrationDTOValidatorSpec extends Specification {

    void "test validate"() {
        given:
        RegistrationDTOValidator validator = new RegistrationDTOValidator()
        RegistrationDTO samePasswords = new RegistrationDTO(password: '123', passwordRepeat: '123')
        RegistrationDTO differentPasswords = new RegistrationDTO(password: '123', passwordRepeat: '456')
        Errors errors = Mock(Errors)

        when:
        validator.validate(differentPasswords, errors)

        then:
        1 * errors.reject("password", "Passwords dont match")

        when:
        validator.validate(samePasswords, errors)

        then:
        0 * errors.reject(_, _)
    }

}
