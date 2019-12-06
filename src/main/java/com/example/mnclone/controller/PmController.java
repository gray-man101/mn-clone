package com.example.mnclone.controller;

import com.example.mnclone.dto.PmDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;

@RestController
@RequestMapping("/pm")
public class PmController {

    @GetMapping
    public Object get(@PageableDefault(page = 0, size = 20)
                      @SortDefault.SortDefaults({
                              @SortDefault(sort = "name", direction = Sort.Direction.DESC),
                              @SortDefault(sort = "id", direction = Sort.Direction.ASC)
                      }) Pageable pageable) {
        return null;
    }

    @PostMapping
    public void create(@RequestBody PmDTO pmDTO) {

    }

    @PutMapping(":id")
    public void update(@PathParam("id") Long id, @RequestBody PmDTO pmDTO) {

    }

    @DeleteMapping(":id")
    public void delete(@PathParam("id") Long id) {

    }

}
