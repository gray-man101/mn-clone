package com.swaperclone.service;

import com.swaperclone.dto.InvestmentDTO;
import com.swaperclone.entity.Investment;
import com.swaperclone.entity.Loan;
import com.swaperclone.entity.LoanStatus;
import com.swaperclone.entity.User;
import com.swaperclone.exception.InsufficientFundsException;
import com.swaperclone.mapper.InvestmentDTOMapper;
import com.swaperclone.repository.InvestmentRepository;
import com.swaperclone.repository.LoanRepository;
import com.swaperclone.repository.UserRepository;
import com.swaperclone.util.ReturnAmountCalculationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class InvestmentService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private InvestmentRepository investmentRepository;
    @Autowired
    private LoanRepository loanRepository;

    @Transactional
    public void invest(Long investorId, Long loanId) {
        User user = userRepository.findRegisteredById(investorId).orElseThrow(RuntimeException::new);
        Loan loan = loanRepository.findNewLoanById(loanId).orElseThrow(RuntimeException::new);

        BigDecimal newBalance = user.getBalance().subtract(loan.getAmount());
        if (BigDecimal.ZERO.compareTo(newBalance) > 0) {
            throw new InsufficientFundsException("Not enough funds to invest");
        }
        user.setBalance(newBalance);
        userRepository.save(user);

        createInvestment(user, loan);

        loan.setStatus(LoanStatus.IN_PROGRESS);
        loanRepository.save(loan);
    }

    private void createInvestment(User user, Loan loan) {
        Investment investment = new Investment();
        investment.setInvestor(user);
        investment.setLoan(loan);
        investment.setAmountToReceive(ReturnAmountCalculationUtils.calculateInvestorReturnAmount(loan.getAmount(),
                loan.getInvestorInterest()));
        investmentRepository.save(investment);
    }

    public Page<InvestmentDTO> getInvestments(Long investorId, Pageable pageable) {
        return investmentRepository.findInvestmentStatuses(investorId, pageable)
                .map(InvestmentDTOMapper::map);
    }

}
