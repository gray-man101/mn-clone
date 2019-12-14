package com.swaperclone.mapper;

import com.swaperclone.dto.LoanDTO;
import com.swaperclone.entity.Loan;

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
