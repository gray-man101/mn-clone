package com.swaperclone.company.dto.validation;

import com.swaperclone.company.dto.LoanDTO;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class LoanDTOValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz == LoanDTO.class;
    }

    @Override
    public void validate(Object target, Errors errors) {
        LoanDTO dto = (LoanDTO) target;
        if (dto.getAmount() != null && dto.getInvestorInterest() != null && dto.getAmountToReturn() != null
                && dto.getAmount().multiply(BigDecimal.valueOf(100).add(dto.getInvestorInterest())
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.DOWN))
                .compareTo(dto.getAmountToReturn()) >= 0) {
            errors.reject("InvalidReturnAmount", "Amount to return must be greater than amount with investor interest");
        }
    }
}
