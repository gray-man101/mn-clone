package com.example.mnclone.service;

import com.example.mnclone.dto.LoanDTO;
import com.example.mnclone.entity.Loan;
import com.example.mnclone.entity.LoanStatus;
import com.example.mnclone.entity.User;
import com.example.mnclone.exception.BadRequestException;
import com.example.mnclone.exception.NotFoundException;
import com.example.mnclone.mapper.AvailableLoanDTOMapper;
import com.example.mnclone.mapper.LoanDTOMapper;
import com.example.mnclone.repository.LoanRepository;
import com.example.mnclone.repository.PaymentRepository;
import com.example.mnclone.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZonedDateTime;

@Service
@Slf4j
public class LoanService {

    @Autowired
    private LoanRepository loanRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private UserRepository userRepository;

    public Page<LoanDTO> getLoans(Pageable pageable) {
        return loanRepository.findAll(pageable).map(LoanDTOMapper::map);
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
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Loan not found"));
        if (loan.getStatus() != LoanStatus.NEW) {
            throw new BadRequestException(String.format("Cannot update loan in status other than %s", LoanStatus.NEW));
        }
        loan.setDebtorName(loanDTO.getDebtorName());
        loan.setAmount(loanDTO.getAmount());
        loan.setAmountToReturn(loanDTO.getAmountToReturn());
        loan.setInvestorInterest(loanDTO.getInvestorInterest());
        loanRepository.save(loan);
    }

    public void delete(Long id) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Loan not found"));
        if (loan.getStatus() != LoanStatus.NEW) {
            throw new BadRequestException(String.format("Cannot delete loan in status other than %s", LoanStatus.NEW));
        }
        loanRepository.deleteById(id);
    }

    @Transactional
    public void setFailedStatus(Long id) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Loan not found"));
        if (loan.getStatus() != LoanStatus.NEW) {
            throw new BadRequestException(String.format("Cannot set loan status to %s to one in status %s",
                    LoanStatus.FAILED, loan.getStatus()));
        }

        refundInvestorPartly(loan);

        loan.setStatus(LoanStatus.FAILED);
        loanRepository.save(loan);
    }

    private void refundInvestorPartly(Loan loan) {
        BigDecimal coefficient = loan.getAmount().multiply(loan.getInvestorInterest())
                .divide(loan.getAmountToReturn(), 2, RoundingMode.DOWN);
        BigDecimal paidAmount = paymentRepository.sumPayments(loan.getId());

        BigDecimal sumToReturnToInvestor = paidAmount.multiply(coefficient);
        log.info(String.format("Partial refund to investor - %f. The rest %f is being transferred to company",
                sumToReturnToInvestor, paidAmount.subtract(sumToReturnToInvestor)));
        User investor = loan.getInvestment().getInvestor();
        investor.setBalance(investor.getBalance().add(sumToReturnToInvestor));
        userRepository.save(investor);
    }
}
