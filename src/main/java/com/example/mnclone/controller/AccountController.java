package com.example.mnclone.controller;

import com.example.mnclone.dto.AccountInfoDTO;
import com.example.mnclone.dto.MoneyRequestDTO;
import com.example.mnclone.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @GetMapping
    public AccountInfoDTO getAccountInfo() {
        return accountService.getAccountInfo();
    }

    @PostMapping("topUp")
    public void topUp(@Valid @RequestBody MoneyRequestDTO moneyRequestDTO) {
        accountService.topUp(moneyRequestDTO.getAmount());
    }

    @PostMapping("withdraw")
    public void withdraw(@Valid @RequestBody MoneyRequestDTO moneyRequestDTO) {
        accountService.withdraw(moneyRequestDTO.getAmount());
    }

}
