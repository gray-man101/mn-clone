package com.example.mnclone.mapper;

import com.example.mnclone.dto.LnDTO;
import com.example.mnclone.entity.Ln;

public class LnDTOMapper {

    public static LnDTO map(Ln ln) {
        LnDTO dto = new LnDTO();
        dto.setId(ln.getId());
        dto.setAmount(ln.getAmount());
        dto.setStatus(ln.getStatus());
        dto.setDbName(ln.getDbName());
        dto.setCreated(ln.getCreated());
        return dto;
    }

}
