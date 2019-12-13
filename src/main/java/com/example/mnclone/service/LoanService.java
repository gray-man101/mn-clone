package com.example.mnclone.service;

import com.example.mnclone.dto.LoanDTO;
import com.example.mnclone.entity.Loan;
import com.example.mnclone.entity.LoanStatus;
import com.example.mnclone.mapper.AvailableLoanDTOMapper;
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

    public Page<LoanDTO> getLoans(Pageable pageable) {
        return loanRepository.findAll(pageable)
                .map(LoanDTOMapper::map);
    }

    public Page<LoanDTO> getAvailableLoans(Pageable pageable) {
        return loanRepository.findNewLoans(pageable).map(AvailableLoanDTOMapper::map);
    }

    public void create(LoanDTO loanDTO) {
        Loan loan = new Loan();
        loan.setDebtorName(loanDTO.getDebtorName());
        loan.setStatus(LoanStatus.NEW);
        loan.setAmount(loanDTO.getAmount());
        loan.setAmountToReturn(loanDTO.getAmountToReturn());
        loan.setInvestorInterest(loanDTO.getInvestorInterest());
        loan.setCreated(ZonedDateTime.now());
        loanRepository.save(loan);
    }

    public void update(Long id, LoanDTO loanDTO) {
        Loan loan = loanRepository.getOne(id);
        loan.setDebtorName(loanDTO.getDebtorName());
        loan.setAmount(loanDTO.getAmount());
        loan.setAmountToReturn(loanDTO.getAmountToReturn());
        loan.setInvestorInterest(loanDTO.getInvestorInterest());
        loanRepository.save(loan);
    }

    @Transactional
    public void delete(Long id) {
        //TODO delete investments, return money, delete loan
        loanRepository.deleteById(id);
    }
}
