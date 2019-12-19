package com.swaperclone.common.repository;

import com.swaperclone.common.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Page<Payment> findByLoanId(Long loanId, Pageable pageable);

    @Modifying
    @Query("delete from Payment p where p.id=:id and p.loan.id=:loanId")
    int deletePayment(@Param("loanId") Long loanId, @Param("id") Long id);

    @Modifying
    @Query("update Payment p set p.amount=:amount where p.id=:id and p.loan.id=:loanId")
    int updatePaymentAmount(@Param("loanId") Long loanId, @Param("id") Long id, @Param("amount") BigDecimal amount);
}
