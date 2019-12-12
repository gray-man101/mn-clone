package com.example.mnclone.service;

import com.example.mnclone.dto.LnDTO;
import com.example.mnclone.entity.Ln;
import com.example.mnclone.entity.LnStatus;
import com.example.mnclone.mapper.LnDTOMapper;
import com.example.mnclone.repository.LnRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Service
public class LnService {

    @Autowired
    private LnRepository lnRepository;

    public Page<LnDTO> getLs(Pageable pageable) {
        return lnRepository.findAll(pageable).map(LnDTOMapper::map);
    }

    public Page<LnDTO> getNewLs(Pageable pageable) {
        return lnRepository.findNewLs(pageable).map(LnDTOMapper::map);
    }

    public void create(LnDTO lnDTO) {
        Ln ln = new Ln();
        ln.setDbName(lnDTO.getDbName());
        ln.setStatus(LnStatus.NEW);
        ln.setAmount(lnDTO.getAmount());
        ln.setAmountToReturn(lnDTO.getAmountToReturn());
        ln.setCreated(ZonedDateTime.now());
        lnRepository.save(ln);
    }

    public void update(Long id, LnDTO lnDTO) {
        Ln ln = lnRepository.getOne(id);
        ln.setDbName(lnDTO.getDbName());
        ln.setAmount(lnDTO.getAmount());
        ln.setAmountToReturn(lnDTO.getAmountToReturn());
        lnRepository.save(ln);
    }

    @Transactional
    public void delete(Long id) {
        //TODO delete investments, return money, delete ln
        lnRepository.deleteById(id);
    }
}
