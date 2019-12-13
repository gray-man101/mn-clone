package com.example.mnclone.controller;

import com.example.mnclone.dto.LoanDTO;
import com.example.mnclone.service.LoanService;
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
@RequestMapping("/api/availableLoans")
public class AvailableLoanController {

    @Autowired
    private LoanService loanService;

    @GetMapping
    public Page<LoanDTO> getAvailableLs(@PageableDefault(page = 0, size = 5)
                                        @SortDefault.SortDefaults({
                                                @SortDefault(sort = "created", direction = Sort.Direction.DESC)
                                        }) Pageable pageable) {
        return loanService.getNewLoans(pageable);
    }

}
