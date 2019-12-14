package com.swaperclone.mnclone.mapper;

import com.swaperclone.mnclone.dto.PaymentDTO;
import com.swaperclone.mnclone.entity.Payment;

public class PaymentDTOMapper {

    public static PaymentDTO map(Payment payment) {
        PaymentDTO dto = new PaymentDTO();
        dto.setId(payment.getId());
        dto.setCreated(payment.getCreated());
        dto.setAmount(payment.getAmount());
        return dto;
    }

}
