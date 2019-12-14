package com.swaperclone.dto.validation;

import com.swaperclone.dto.RegistrationDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class RegistrationDTOValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return clazz == RegistrationDTO.class;
    }

    @Override
    public void validate(Object target, Errors errors) {
        RegistrationDTO dto = (RegistrationDTO) target;
        if (dto.getPassword() != null && dto.getPasswordRepeat() != null
                && !StringUtils.equals(dto.getPassword(), dto.getPasswordRepeat())) {
            errors.reject("password", "Passwords dont match");
        }
    }
}
