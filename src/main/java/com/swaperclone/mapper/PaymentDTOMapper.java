package com.swaperclone.mapper;

import com.swaperclone.dto.PaymentDTO;
import com.swaperclone.entity.Payment;

public class PaymentDTOMapper {

    public static PaymentDTO map(Payment payment) {
        PaymentDTO dto = new PaymentDTO();
        dto.setId(payment.getId());
        dto.setCreated(payment.getCreated());
        dto.setAmount(payment.getAmount());
        return dto;
    }

}
