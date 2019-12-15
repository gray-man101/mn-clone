package com.swaperclone.company.mapper;

import com.swaperclone.company.dto.PaymentDTO;
import com.swaperclone.common.entity.Payment;

public class PaymentDTOMapper {

    public static PaymentDTO map(Payment payment) {
        PaymentDTO dto = new PaymentDTO();
        dto.setId(payment.getId());
        dto.setCreated(payment.getCreated());
        dto.setAmount(payment.getAmount());
        return dto;
    }

}
