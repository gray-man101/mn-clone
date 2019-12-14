package com.example.mnclone.controller;

import com.example.mnclone.config.MnCloneAuthenticationToken;
import com.example.mnclone.dto.AccountInfoDTO;
import com.example.mnclone.dto.MoneyRequestDTO;
import com.example.mnclone.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@PreAuthorize("hasRole('ROLE_CUSTOMER')")
@RequestMapping("/api/account")
public class AccountController {

    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public AccountInfoDTO getAccountInfo(MnCloneAuthenticationToken auth) {
        return accountService.getAccountInfo(auth.getUserId());
    }

    @PostMapping("topUp")
    public void topUp(MnCloneAuthenticationToken auth, @Valid @RequestBody MoneyRequestDTO moneyRequestDTO) {
        accountService.topUp(auth.getUserId(), moneyRequestDTO.getAmount());
    }

    @PostMapping("withdraw")
    public void withdraw(MnCloneAuthenticationToken auth, @Valid @RequestBody MoneyRequestDTO moneyRequestDTO) {
        accountService.withdraw(auth.getUserId(), moneyRequestDTO.getAmount());
    }

}
