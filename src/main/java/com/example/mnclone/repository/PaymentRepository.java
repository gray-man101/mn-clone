package com.example.mnclone.repository;

import com.example.mnclone.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Page<Payment> findByLoanId(Long loanId, Pageable pageable);

    @Transactional
    @Query("delete from Payment p where exists (select p1.id from Payment p1 where p1.id=:id and p1.loan.id=:loanId)")
    void deletePayment(@Param("id") Long id, @Param("loanId") Long loanId);

    @Query("select coalesce(sum(p.amount), 0) from Payment p where p.loan.id=:loanId")
    BigDecimal sumPayments(@Param("loanId") Long loanId);
}
