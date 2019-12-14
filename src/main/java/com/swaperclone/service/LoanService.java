package com.swaperclone.service;

import com.swaperclone.dto.LoanDTO;
import com.swaperclone.entity.Loan;
import com.swaperclone.entity.LoanStatus;
import com.swaperclone.entity.User;
import com.swaperclone.exception.BadRequestException;
import com.swaperclone.exception.NotFoundException;
import com.swaperclone.info.FailedLoanInfo;
import com.swaperclone.mapper.AvailableLoanDTOMapper;
import com.swaperclone.mapper.LoanDTOMapper;
import com.swaperclone.repository.LoanRepository;
import com.swaperclone.repository.PaymentRepository;
import com.swaperclone.repository.UserRepository;
import com.swaperclone.util.ReturnAmountCalculationUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
    public FailedLoanInfo setFailedStatus(Long id) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Loan not found"));
        if (loan.getStatus() != LoanStatus.IN_PROGRESS) {
            throw new BadRequestException(String.format("Cannot set loan status to %s to one in status %s",
                    LoanStatus.FAILED, loan.getStatus()));
        }

        FailedLoanInfo result = refundInvestorPartly(loan);

        loan.setStatus(LoanStatus.FAILED);
        loanRepository.save(loan);

        return result;
    }

    private FailedLoanInfo refundInvestorPartly(Loan loan) {
        BigDecimal paidAmount = paymentRepository.sumPayments(loan.getId());

        BigDecimal sumToReturnToInvestor = ReturnAmountCalculationUtils.calculatePartialRefundAmount(paidAmount,
                loan.getAmount(), loan.getAmountToReturn(), loan.getInvestorInterest());
        log.info(String.format("Partial refund to investor - %.2f. The rest %.2f is being transferred to company",
                sumToReturnToInvestor, paidAmount.subtract(sumToReturnToInvestor)));
        User investor = loan.getInvestment().getInvestor();
        investor.setBalance(investor.getBalance().add(sumToReturnToInvestor));
        userRepository.save(investor);

        return new FailedLoanInfo(investor.getEmail(), sumToReturnToInvestor);
    }
}
