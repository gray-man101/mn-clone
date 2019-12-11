package com.example.mnclone.controller;

import com.example.mnclone.config.MnCloneAuthenticationToken;
import com.example.mnclone.dto.IvstDTO;
import com.example.mnclone.dto.IvstStatusDTO;
import com.example.mnclone.service.IvstService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@PreAuthorize("hasRole('CUSTOMER')")
@RequestMapping("/api/ivst")
public class IvstController {

    @Autowired
    private IvstService ivstService;

    @GetMapping
    public Page<IvstDTO> getIvsts(MnCloneAuthenticationToken auth, @PageableDefault(page = 0, size = 5) Pageable pageable) {
        return ivstService.getIvsts(auth.getUserId(), pageable);
    }

    @GetMapping("{ivstId}")
    public IvstStatusDTO getIvstStatus(MnCloneAuthenticationToken auth, @PathVariable Long ivstId) {
        return ivstService.getIvsStatus(auth.getUserId(), ivstId);
    }

    @PostMapping("{lnId}")
    public void ivst(MnCloneAuthenticationToken auth, @PathVariable Long lnId) {
        ivstService.ivst(auth.getUserId(), lnId);
    }

}
