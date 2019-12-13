package com.example.mnclone.controller;

import com.example.mnclone.dto.LoanDTO;
import com.example.mnclone.service.LoanService;
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
@RequestMapping("/api/loan")
public class LoanController {

    @Autowired
    private LoanService loanService;

    @GetMapping
    public Page<LoanDTO> get(@PageableDefault(page = 0, size = 5)
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

    @DeleteMapping("{id}")
    public void delete(@PathVariable Long id) {
        loanService.delete(id);
        //TODO notify user
    }

}
