package com.swaperclone.company.service;

import com.swaperclone.company.dto.LoanDTO;
import com.swaperclone.common.entity.Loan;
import com.swaperclone.common.entity.LoanStatus;
import com.swaperclone.common.entity.User;
import com.swaperclone.common.exception.NotFoundException;
import com.swaperclone.company.info.FailedLoanInfo;
import com.swaperclone.customer.mapper.AvailableLoanDTOMapper;
import com.swaperclone.company.mapper.LoanDTOMapper;
import com.swaperclone.company.model.InProgressLoanModel;
import com.swaperclone.common.repository.LoanRepository;
import com.swaperclone.common.repository.UserRepository;
import com.swaperclone.company.util.ReturnAmountCalculationUtils;
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
    private UserRepository userRepository;

    public Page<LoanDTO> getLoans(Pageable pageable) {
        return loanRepository.findAll(pageable).map(LoanDTOMapper::map);
    }

    public Page<LoanDTO> getAvailableLoans(Pageable pageable) {
        return loanRepository.findNewLoans(pageable).map(AvailableLoanDTOMapper::map);
    }

    @Transactional
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

    @Transactional
    public void update(Long id, LoanDTO loanDTO) {
        Loan loan = loanRepository.findNewLoan(id)
                .orElseThrow(() -> new NotFoundException(String.format("Cannot update loan %d. Only ones in status %s can be updated", id, LoanStatus.NEW)));
        loan.setDebtorName(loanDTO.getDebtorName());
        loan.setAmount(loanDTO.getAmount());
        loan.setAmountToReturn(loanDTO.getAmountToReturn());
        loan.setInvestorInterest(loanDTO.getInvestorInterest());
        loanRepository.save(loan);
    }

    @Transactional
    public void delete(Long id) {
        int deletedObjects = loanRepository.deleteNewLoan(id);
        if (deletedObjects < 1) {
            throw new NotFoundException(String.format("Cannot delete loan %d. Only ones in status %s can be updated", id, LoanStatus.NEW));
        }
    }

    @Transactional
    public FailedLoanInfo setFailedStatus(Long id) {
        InProgressLoanModel inProgressLoanModel = loanRepository.findLoanInProgress(id)
                .orElseThrow(() -> new NotFoundException(String.format("Cannot set loan %d status to failed. Only ones in status %s can be updated", id, LoanStatus.IN_PROGRESS)));
        FailedLoanInfo result = refundInvestorPartly(inProgressLoanModel);
        loanRepository.updateLoanStatusToFailed(id);

        return result;
    }

    private FailedLoanInfo refundInvestorPartly(InProgressLoanModel model) {
        BigDecimal sumToReturnToInvestor = ReturnAmountCalculationUtils.calculatePartialRefundAmount(model.getPaidAmount(),
                model.getAmount(), model.getAmountToReturn(), model.getInvestorInterest());
        log.info(String.format("Partial refund to investor - %.2f. The rest %.2f is being transferred to company",
                sumToReturnToInvestor, model.getPaidAmount().subtract(sumToReturnToInvestor)));
        User investor = userRepository.getOne(model.getInvestorId());
        investor.setBalance(investor.getBalance().add(sumToReturnToInvestor));
        userRepository.save(investor);

        return new FailedLoanInfo(investor.getEmail(), sumToReturnToInvestor);
    }
}
