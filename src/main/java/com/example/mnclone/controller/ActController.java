package com.example.mnclone.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/act")
public class ActController {

    @GetMapping
    public void getAct() {
    }

    @PostMapping("topUp")
    public void topUp() {

    }

    @PostMapping("withdraw")
    public void withdraw() {

    }

    @PostMapping("invest")
    public void invest() {

    }

}
