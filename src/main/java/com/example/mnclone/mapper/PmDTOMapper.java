package com.example.mnclone.mapper;

import com.example.mnclone.dto.PmDTO;
import com.example.mnclone.entity.Pm;

public class PmDTOMapper {

    public static PmDTO map(Pm pm) {
        PmDTO dto = new PmDTO();
        dto.setId(pm.getId());
        dto.setCreated(pm.getCreated());
        dto.setAmount(pm.getAmount());
        return dto;
    }

}
