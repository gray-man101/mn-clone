package com.example.mnclone.service;

import com.example.mnclone.dto.PaymentDTO;
import com.example.mnclone.entity.LoanStatus;
import com.example.mnclone.entity.Loan;
import com.example.mnclone.entity.Payment;
import com.example.mnclone.exception.NotFoundException;
import com.example.mnclone.exception.UnexpectedLoanStatusException;
import com.example.mnclone.mapper.PaymentDTOMapper;
import com.example.mnclone.repository.LoanRepository;
import com.example.mnclone.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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

    public void create(Long loanId, PaymentDTO paymentDTO) {
        Loan loan = loanRepository.findById(loanId).orElseThrow(NotFoundException::new);
        if (loan.getStatus() != LoanStatus.IN_PROGRESS) {
            throw new UnexpectedLoanStatusException();
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
