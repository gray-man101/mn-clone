package com.swaperclone.customer.controller;

import com.swaperclone.company.dto.LoanDTO;
import com.swaperclone.company.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@PreAuthorize("hasRole('ROLE_CUSTOMER')")
@RequestMapping("/api/availableLoan")
public class AvailableLoanController {

    private final LoanService loanService;

    @Autowired
    public AvailableLoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @GetMapping
    public Page<LoanDTO> getAvailableLoans(@PageableDefault(page = 0, size = 50)
                                           @SortDefault.SortDefaults({
                                                   @SortDefault(sort = "created", direction = Sort.Direction.DESC)
                                           }) Pageable pageable) {
        return loanService.getAvailableLoans(pageable);
    }

}
