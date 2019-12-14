package com.swaperclone.service;

import com.swaperclone.dto.LoanDTO;
import com.swaperclone.entity.Loan;
import com.swaperclone.entity.LoanStatus;
import com.swaperclone.entity.User;
import com.swaperclone.exception.NotFoundException;
import com.swaperclone.info.FailedLoanInfo;
import com.swaperclone.mapper.AvailableLoanDTOMapper;
import com.swaperclone.mapper.LoanDTOMapper;
import com.swaperclone.model.InProgressLoanModel;
import com.swaperclone.repository.LoanRepository;
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
                .orElseThrow(() -> new NotFoundException("Loan not found"));
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
            throw new NotFoundException(String.format("Loan %d in status %s not found", id, LoanStatus.NEW));
        }
    }

    @Transactional
    public FailedLoanInfo setFailedStatus(Long id) {
        InProgressLoanModel inProgressLoanModel = loanRepository.findLoanInProgress(id)
                .orElseThrow(() -> new NotFoundException("Loan not found"));
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
