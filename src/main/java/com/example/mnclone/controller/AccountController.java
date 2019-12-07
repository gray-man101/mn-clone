package com.example.mnclone.controller;

import com.example.mnclone.dto.AccountInfoDTO;
import com.example.mnclone.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public void topUp() {

    }

    @PostMapping("withdraw")
    public void withdraw() {

    }

}
