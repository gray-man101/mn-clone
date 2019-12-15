package com.swaperclone.customer.service;

import com.swaperclone.customer.dto.InvestmentDTO;
import com.swaperclone.common.entity.Investment;
import com.swaperclone.common.entity.Loan;
import com.swaperclone.common.entity.LoanStatus;
import com.swaperclone.common.entity.User;
import com.swaperclone.common.exception.InsufficientFundsException;
import com.swaperclone.common.exception.NotFoundException;
import com.swaperclone.customer.mapper.InvestmentDTOMapper;
import com.swaperclone.common.repository.InvestmentRepository;
import com.swaperclone.common.repository.LoanRepository;
import com.swaperclone.common.repository.UserRepository;
import com.swaperclone.company.util.ReturnAmountCalculationUtils;
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
        User user = userRepository.findRegisteredById(investorId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Loan loan = loanRepository.findNewLoan(loanId)
                .orElseThrow(() -> new NotFoundException("Loan not found"));

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
