package com.example.mnclone.controller;

import com.example.mnclone.dto.PmDTO;
import com.example.mnclone.service.PmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/pm")
@Validated
public class PmController {

    @Autowired
    private PmService pmService;

    @GetMapping("{id}")
    public PmDTO get(@PathVariable("id") Long id) {
        return pmService.findPm(id);
    }

    @PostMapping
    public void create(@Valid @RequestBody PmDTO pmDTO) {
        pmService.create(pmDTO);
    }

    @PutMapping("{id}")
    public void update(@PathVariable("id") Long id, @RequestBody PmDTO pmDTO) {
        pmService.update(id, pmDTO);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable("id") Long id) {
        pmService.delete(id);
    }

}
