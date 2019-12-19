package com.swaperclone.company.controller;

import com.swaperclone.company.dto.PaymentDTO;
import com.swaperclone.company.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@PreAuthorize("hasRole('ROLE_COMPANY_ADMIN')")
@RequestMapping("/api/loan/{loanId}/payment")
public class PaymentController {

    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping
    public Page<PaymentDTO> get(@PathVariable Long loanId,
                                @PageableDefault(page = 0, size = 50)
                                @SortDefault.SortDefaults({
                                        @SortDefault(sort = "created", direction = Sort.Direction.DESC)
                                }) Pageable pageable) {
        return paymentService.findPayments(loanId, pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@PathVariable Long loanId, @Valid @RequestBody PaymentDTO paymentDTO) {
        paymentService.create(loanId, paymentDTO);
    }

    @PutMapping("{id}")
    public void update(@PathVariable Long loanId, @PathVariable Long id, @Valid @RequestBody PaymentDTO paymentDTO) {
        paymentService.update(loanId, id, paymentDTO);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable Long loanId, @PathVariable("id") Long id) {
        paymentService.delete(loanId, id);
    }

}
