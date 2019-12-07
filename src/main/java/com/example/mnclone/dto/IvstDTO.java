package com.example.mnclone.dto;

import lombok.Data;

import java.util.List;

@Data
public class IvstDTO {
    private LnDTO lnDTO;
    private List<PmDTO> ps;
}
