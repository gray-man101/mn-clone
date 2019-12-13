package com.example.mnclone.repository;

import com.example.mnclone.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Page<Payment> findByLoanId(Long loanId, Pageable pageable);

    @Transactional
    @Query("delete from Payment p where exists (select p1.id from Payment p1 where p1.id=:id and p1.loan.id=:loanId)")
    void deletePayment(Long id, Long loanId);
}
