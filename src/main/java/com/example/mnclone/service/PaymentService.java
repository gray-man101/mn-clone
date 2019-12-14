package com.example.mnclone.service;

import com.example.mnclone.dto.PaymentDTO;
import com.example.mnclone.entity.Loan;
import com.example.mnclone.entity.LoanStatus;
import com.example.mnclone.entity.Payment;
import com.example.mnclone.exception.BadRequestException;
import com.example.mnclone.exception.NotFoundException;
import com.example.mnclone.mapper.PaymentDTOMapper;
import com.example.mnclone.repository.LoanRepository;
import com.example.mnclone.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private LoanRepository loanRepository;

    public Page<PaymentDTO> findPayments(Long loanId, Pageable pageable) {
        return paymentRepository.findByLoanId(loanId, pageable).map(PaymentDTOMapper::map);
    }

    @Transactional
    public void create(Long loanId, PaymentDTO paymentDTO) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new NotFoundException("Loan not found"));
        if (loan.getStatus() != LoanStatus.IN_PROGRESS) {
            throw new BadRequestException(String.format("Cannot add payments to loans in status other than %s", LoanStatus.IN_PROGRESS));
        }

        BigDecimal currentPaidSum = paymentRepository.sumPayments(loanId);
        if (currentPaidSum.add(paymentDTO.getAmount()).compareTo(loan.getAmountToReturn()) > 0) {
            throw new BadRequestException("Payment sum exceeds loan amount to return");
        }

        if (currentPaidSum.add(paymentDTO.getAmount()).compareTo(loan.getAmountToReturn()) == 0) {
            loan.setStatus(LoanStatus.COMPLETE);
            loanRepository.save(loan);
        }

        Payment payment = new Payment();
        payment.setAmount(paymentDTO.getAmount());
        payment.setLoan(loan);
        payment.setCreated(ZonedDateTime.now());
        paymentRepository.save(payment);
    }

    public void update(Long loanId, Long id, PaymentDTO paymentDTO) {
        Payment payment = paymentRepository.getOne(id);
        payment.setAmount(paymentDTO.getAmount());
        payment.setLoan(loanRepository.getOne(loanId));
        paymentRepository.save(payment);
    }

    public void delete(Long loanId, Long id) {
        paymentRepository.deletePayment(id, loanId);
    }

}
