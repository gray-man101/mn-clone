package com.example.mnclone.service;

import com.example.mnclone.dto.PmDTO;
import com.example.mnclone.entity.Pm;
import com.example.mnclone.mapper.PmDTOMapper;
import com.example.mnclone.repository.LnRepository;
import com.example.mnclone.repository.PmRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

@Service
public class PmService {

    @Autowired
    private PmRepository pmRepository;
    @Autowired
    private LnRepository lnRepository;

    public Page<PmDTO> findPs(Long lnId, Pageable pageable) {
        //TODO lnId doesn't exist
        return pmRepository.findByLnId(lnId, pageable).map(PmDTOMapper::map);
    }

    public void create(Long lnId, PmDTO pmDTO) {
        Pm pm = new Pm();
        pm.setAmount(pmDTO.getAmount());
        pm.setLn(lnRepository.getOne(lnId));
        pm.setCreated(ZonedDateTime.now());
        pmRepository.save(pm);
    }

    public void update(Long lnId, Long id, PmDTO pmDTO) {
        Pm pm = pmRepository.getOne(id);
        pm.setAmount(pmDTO.getAmount());
        pm.setLn(lnRepository.getOne(lnId));
        pmRepository.save(pm);
    }

    public void delete(Long lnId, Long id) {
        pmRepository.deleteByLnIdAndId(lnId, id);
    }

}
