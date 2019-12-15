package com.swaperclone.customer.mapper;

import com.swaperclone.company.dto.LoanDTO;
import com.swaperclone.common.entity.Loan;

public class AvailableLoanDTOMapper {

    public static LoanDTO map(Loan loan) {
        LoanDTO dto = new LoanDTO();
        dto.setId(loan.getId());
        dto.setAmount(loan.getAmount());
        dto.setInvestorInterest(loan.getInvestorInterest());
        dto.setStatus(loan.getStatus());
        dto.setDebtorName(loan.getDebtorName());
        dto.setCreated(loan.getCreated());
        return dto;
    }

}
