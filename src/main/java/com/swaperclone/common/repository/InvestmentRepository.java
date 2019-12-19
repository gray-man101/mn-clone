package com.swaperclone.common.repository;

import com.swaperclone.common.entity.Investment;
import com.swaperclone.customer.model.InvestmentStatusModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface InvestmentRepository extends JpaRepository<Investment, Long> {

    Optional<Investment> findByLoanId(Long loanId);

    @Query("select i.id as id, i.amountToReceive as amountToReceive, l.debtorName as debtorName, l.amount as amount, " +
            "  count(p.id) as payments, coalesce(sum(p.amount), 0)/l.amountToReturn as percentageComplete " +
            "from Investment i " +
            "join i.loan as l " +
            "left join l.payments as p " +
            "where i.investor.id=:investorId and l.status=com.swaperclone.common.entity.LoanStatus.IN_PROGRESS " +
            "group by i.id")
    Page<InvestmentStatusModel> findInvestmentStatuses(@Param("investorId") Long investorId, Pageable pageable);

}
