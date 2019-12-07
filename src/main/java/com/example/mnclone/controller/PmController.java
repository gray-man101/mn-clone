package com.example.mnclone.controller;

import com.example.mnclone.dto.PmDTO;
import com.example.mnclone.service.PmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/ln/{lnId}/pm")
@Validated
public class PmController {

    @Autowired
    private PmService pmService;

    @GetMapping
    public Page<PmDTO> get(@PathVariable Long lnId,
                           @PageableDefault(page = 0, size = 5)
                           @SortDefault.SortDefaults({
                                   @SortDefault(sort = "created", direction = Sort.Direction.DESC)
                           }) Pageable pageable) {
        return pmService.findPs(lnId, pageable);
    }

    @PostMapping
    public void create(@PathVariable Long lnId, @Valid @RequestBody PmDTO pmDTO) {
        pmService.create(lnId, pmDTO);
    }

    @PutMapping("{id}")
    public void update(@PathVariable Long lnId, @PathVariable Long id, @RequestBody PmDTO pmDTO) {
        pmService.update(lnId, id, pmDTO);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable Long lnId, @PathVariable("id") Long id) {
        pmService.delete(lnId, id);
    }

}
