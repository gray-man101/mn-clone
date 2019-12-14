package com.example.mnclone.mapper;

import com.example.mnclone.dto.LoanDTO;
import com.example.mnclone.entity.Loan;

public class LoanDTOMapper {

    public static LoanDTO map(Loan loan) {
        LoanDTO dto = new LoanDTO();
        dto.setId(loan.getId());
        dto.setAmount(loan.getAmount());
        dto.setAmountToReturn(loan.getAmountToReturn());
        dto.setInvestorInterest(loan.getInvestorInterest());
        dto.setStatus(loan.getStatus());
        dto.setDebtorName(loan.getDebtorName());
        dto.setCreated(loan.getCreated());
        return dto;
    }

}