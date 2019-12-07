package com.example.mnclone.controller;

import com.example.mnclone.dto.LnDTO;
import com.example.mnclone.service.LnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/ln")
public class LnController {

    @Autowired
    private LnService lnService;

    @GetMapping
    public Page<LnDTO> get(@PageableDefault(page = 0, size = 5)
                           @SortDefault.SortDefaults({
                                   @SortDefault(sort = "dbName", direction = Sort.Direction.ASC)
                           }) Pageable pageable) {
        return lnService.getLns(pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@Valid @RequestBody LnDTO lnDTO) {
        lnService.create(lnDTO);
    }

    @PutMapping("{id}")
    public void update(@PathVariable Long id, @RequestBody LnDTO lnDTO) {
        lnService.update(id, lnDTO);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable Long id) {
        lnService.delete(id);
    }

}
