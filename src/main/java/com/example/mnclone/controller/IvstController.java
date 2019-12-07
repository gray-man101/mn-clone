package com.example.mnclone.controller;

import com.example.mnclone.dto.IvstDTO;
import com.example.mnclone.service.IvstService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("ivst")
public class IvstController {

    @Autowired
    private IvstService ivstService;

    @GetMapping
    public Page<IvstDTO> getIvsts(@PageableDefault(page = 0, size = 5) Pageable pageable) {
        return ivstService.getIvsts(pageable);
    }

    @PostMapping("{lnId}")
    public void ivst(@PathVariable Long lnId) {
        ivstService.ivst(lnId);
    }

}
