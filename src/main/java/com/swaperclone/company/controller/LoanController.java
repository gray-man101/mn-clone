package com.swaperclone.company.controller;

import com.swaperclone.company.dto.LoanDTO;
import com.swaperclone.company.dto.validation.LoanDTOValidator;
import com.swaperclone.company.info.FailedLoanInfo;
import com.swaperclone.company.service.EmailService;
import com.swaperclone.company.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@PreAuthorize("hasRole('ROLE_COMPANY_ADMIN')")
@RequestMapping("/api/loan")
public class LoanController {

    private final LoanService loanService;
    private final LoanDTOValidator loanDTOValidator;
    private final EmailService emailService;

    @Autowired
    public LoanController(LoanDTOValidator loanDTOValidator, LoanService loanService, EmailService emailService) {
        this.loanDTOValidator = loanDTOValidator;
        this.loanService = loanService;
        this.emailService = emailService;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.addValidators(loanDTOValidator);
    }

    @GetMapping
    public Page<LoanDTO> get(@PageableDefault(page = 0, size = 50)
                             @SortDefault.SortDefaults({
                                     @SortDefault(sort = "created", direction = Sort.Direction.DESC)
                             }) Pageable pageable) {
        return loanService.getLoans(pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@Valid @RequestBody LoanDTO loanDTO) {
        loanService.create(loanDTO);
    }

    @PutMapping("{id}")
    public void update(@PathVariable Long id, @Valid @RequestBody LoanDTO loanDTO) {
        loanService.update(id, loanDTO);
    }

    @PostMapping("{id}/fail")
    public void setFailedStatus(@PathVariable Long id) {
        FailedLoanInfo result = loanService.setFailedStatus(id);
        emailService.notifyCustomerAboutPartialRefund(result.getInvestorEmail(), result.getPartialRefundAmount());
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable Long id) {
        loanService.delete(id);
    }

}
