package com.example.mnclone.controller;

import com.example.mnclone.dto.LnDTO;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/availableLs")
public class AvailableLnController {

    @GetMapping
    public Page<LnDTO> getAvailableLs() {
        return Page.empty();
    }

}
