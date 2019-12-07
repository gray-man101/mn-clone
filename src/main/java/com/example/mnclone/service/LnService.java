package com.example.mnclone.service;

import com.example.mnclone.dto.LnDTO;
import com.example.mnclone.entity.Ln;
import com.example.mnclone.entity.LnStatus;
import com.example.mnclone.repository.LnRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class LnService {

    @Autowired
    private LnRepository lnRepository;

    public Page<LnDTO> getLns(Pageable pageable) {
        return lnRepository.findAll(pageable).map(ln -> {
            LnDTO lnDTO = new LnDTO();
            lnDTO.setId(ln.getId());
            lnDTO.setAmount(ln.getAmount());
            lnDTO.setStatus(ln.getStatus());
            lnDTO.setDbName(ln.getDbName());
            lnDTO.setCreated(ln.getCreated());
            return lnDTO;
        });
    }

    public void create(LnDTO lnDTO) {
        Ln ln = new Ln();
        ln.setDbName(lnDTO.getDbName());
        ln.setStatus(LnStatus.NEW);
        ln.setAmount(lnDTO.getAmount());
        lnRepository.save(ln);
    }

    public void update(Long id, LnDTO lnDTO) {
        Ln ln = lnRepository.getOne(id);
        ln.setDbName(lnDTO.getDbName());
        ln.setStatus(lnDTO.getStatus());
        ln.setAmount(lnDTO.getAmount());
        lnRepository.save(ln);
    }

    public void delete(Long id) {
        lnRepository.deleteById(id);
    }
}
