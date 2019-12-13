package com.example.mnclone.mapper;

import com.example.mnclone.dto.InvestmentDTO;
import com.example.mnclone.model.InvestmentStatusModel;

public class InvestmentDTOMapper {

    public static InvestmentDTO map(InvestmentStatusModel model) {
        InvestmentDTO dto = new InvestmentDTO();
        dto.setId(model.getId());
        dto.setDebtorName(model.getDebtorName());
        dto.setPayments(model.getPayments());
        dto.setPaidAmount(model.getPaidAmount());
        dto.setOverallAmount(model.getAmount());
        dto.setAmountToReceive(model.getAmountToReceive());
        return dto;
    }

}
