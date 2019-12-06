package com.example.mnclone.service;

import com.example.mnclone.dto.PmDTO;
import com.example.mnclone.entity.Pm;
import com.example.mnclone.repository.LnRepository;
import com.example.mnclone.repository.PmRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PmService {

    @Autowired
    private PmRepository pmRepository;
    @Autowired
    private LnRepository lnRepository;

    public PmDTO findPm(Long id) {
        return pmRepository.findById(id)
                .map(pm -> {
                    PmDTO dto = new PmDTO();
                    dto.setId(pm.getId());
                    //TODO follow up requests
                    dto.setLnId(pm.getLn().getId());
                    dto.setAmount(pm.getAmount());
                    return dto;
                })
                .orElse(null);
    }

    public void create(PmDTO pmDTO) {
        Pm pm = new Pm();
        pm.setAmount(pmDTO.getAmount());
        pm.setLn(lnRepository.getOne(pmDTO.getLnId()));
        pmRepository.save(pm);
    }

    public void update(Long id, PmDTO pmDTO) {
        Pm pm = pmRepository.getOne(id);
        pm.setAmount(pmDTO.getAmount());
        pm.setLn(lnRepository.getOne(pmDTO.getLnId()));
        pmRepository.save(pm);
    }

    public void delete(Long id) {
        pmRepository.deleteById(id);
    }

}
