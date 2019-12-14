package com.swaperclone.mnclone.controller;

import com.swaperclone.mnclone.config.MnCloneAuthenticationToken;
import com.swaperclone.mnclone.dto.InvestmentDTO;
import com.swaperclone.mnclone.service.InvestmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@PreAuthorize("hasRole('ROLE_CUSTOMER')")
@RequestMapping("/api/investment")
public class InvestmentController {

    private final InvestmentService investmentService;

    @Autowired
    public InvestmentController(InvestmentService investmentService) {
        this.investmentService = investmentService;
    }

    @GetMapping
    public Page<InvestmentDTO> getInvestments(MnCloneAuthenticationToken auth, @PageableDefault(page = 0, size = 5) Pageable pageable) {
        return investmentService.getInvestments(auth.getUserId(), pageable);
    }

    @PostMapping("{loanId}")
    public void invest(MnCloneAuthenticationToken auth, @PathVariable Long loanId) {
        investmentService.invest(auth.getUserId(), loanId);
    }

}
