package com.example.mnclone.mapper;

import com.example.mnclone.dto.PaymentDTO;
import com.example.mnclone.entity.Payment;

public class PaymentDTOMapper {

    public static PaymentDTO map(Payment payment) {
        PaymentDTO dto = new PaymentDTO();
        dto.setId(payment.getId());
        dto.setCreated(payment.getCreated());
        dto.setAmount(payment.getAmount());
        return dto;
    }

}
