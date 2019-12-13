package com.example.mnclone.service;

import com.example.mnclone.dto.LoanDTO;
import com.example.mnclone.entity.Loan;
import com.example.mnclone.entity.LoanStatus;
import com.example.mnclone.mapper.LoanDTOMapper;
import com.example.mnclone.repository.LoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Service
public class LoanService {

    @Autowired
    private LoanRepository loanRepository;

    public Page<LoanDTO> getLs(Pageable pageable) {
        return loanRepository.findAll(pageable)
                .map(LoanDTOMapper::map);
    }

    public Page<LoanDTO> getNewLoans(Pageable pageable) {
        return loanRepository.findNewLoans(pageable).map(LoanDTOMapper::map);
    }

    public void create(LoanDTO loanDTO) {
        Loan loan = new Loan();
        loan.setDebtorName(loanDTO.getDbName());
        loan.setStatus(LoanStatus.NEW);
        loan.setAmount(loanDTO.getAmount());
        loan.setAmountToReturn(loanDTO.getAmountToReturn());
        loan.setCreated(ZonedDateTime.now());
        loanRepository.save(loan);
    }

    public void update(Long id, LoanDTO loanDTO) {
        Loan loan = loanRepository.getOne(id);
        loan.setDebtorName(loanDTO.getDbName());
        loan.setAmount(loanDTO.getAmount());
        loan.setAmountToReturn(loanDTO.getAmountToReturn());
        loanRepository.save(loan);
    }

    @Transactional
    public void delete(Long id) {
        //TODO delete investments, return money, delete loan
        loanRepository.deleteById(id);
    }
}
