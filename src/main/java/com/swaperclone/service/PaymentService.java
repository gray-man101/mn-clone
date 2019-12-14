package com.swaperclone.service;

import com.swaperclone.dto.PaymentDTO;
import com.swaperclone.entity.*;
import com.swaperclone.exception.BadRequestException;
import com.swaperclone.exception.NotFoundException;
import com.swaperclone.mapper.PaymentDTOMapper;
import com.swaperclone.model.InProgressLoanModel;
import com.swaperclone.repository.InvestmentRepository;
import com.swaperclone.repository.LoanRepository;
import com.swaperclone.repository.PaymentRepository;
import com.swaperclone.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private LoanRepository loanRepository;
    @Autowired
    private InvestmentRepository investmentRepository;
    @Autowired
    private UserRepository userRepository;

    public Page<PaymentDTO> findPayments(Long loanId, Pageable pageable) {
        return paymentRepository.findByLoanId(loanId, pageable).map(PaymentDTOMapper::map);
    }

    @Transactional
    public void create(Long loanId, PaymentDTO paymentDTO) {
        InProgressLoanModel loanModel = loanRepository.findLoanInProgress(loanId)
                .orElseThrow(() -> new NotFoundException("Loan not found"));
        if (loanModel.getPaidAmount().add(paymentDTO.getAmount()).compareTo(loanModel.getAmountToReturn()) > 0) {
            throw new BadRequestException("Payment sum exceeds loan amount to return");
        }

        Loan loan = loanRepository.getOne(loanId);
        if (loanModel.getPaidAmount().add(paymentDTO.getAmount()).compareTo(loanModel.getAmountToReturn()) == 0) {
            finalizeLoan(loan);
        }

        Payment payment = new Payment();
        payment.setAmount(paymentDTO.getAmount());
        payment.setLoan(loan);
        payment.setCreated(ZonedDateTime.now());
        paymentRepository.save(payment);
    }

    private void finalizeLoan(Loan loan) {
        Investment investment = investmentRepository.findByLoanId(loan.getId())
                .orElseThrow(() -> new NotFoundException("Investment not found"));
        User investor = investment.getInvestor();
        investor.setBalance(investor.getBalance().add(investment.getAmountToReceive()));
        userRepository.save(investor);
        loan.setStatus(LoanStatus.COMPLETE);
        loanRepository.save(loan);
    }

    @Transactional
    public void update(Long loanId, Long id, PaymentDTO paymentDTO) {
        int updatedObjects = paymentRepository.updatePaymentAmount(id, loanId, paymentDTO.getAmount());
        if (updatedObjects < 1) {
            throw new NotFoundException(String.format("Payment %d not found for loan %d", id, loanId));
        }
    }

    @Transactional
    public void delete(Long loanId, Long id) {
        int deletedObjects = paymentRepository.deletePayment(id, loanId);
        if (deletedObjects < 1) {
            throw new NotFoundException(String.format("Payment %d for loan %d not found", id, loanId));
        }
    }

}
