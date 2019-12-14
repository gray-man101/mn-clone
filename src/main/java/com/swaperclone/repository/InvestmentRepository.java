package com.swaperclone.repository;

import com.swaperclone.entity.Investment;
import com.swaperclone.model.InvestmentStatusModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InvestmentRepository extends JpaRepository<Investment, Long> {

    @Query("select i from Investment i where i.investor.id=:userId order by i.loan.created desc")
    Page<Investment> findByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("select i.id as id, i.amountToReceive as amountToReceive, l.debtorName as debtorName, " +
            "   l.amount as amount, count(p.id) as payments, coalesce(sum(p.amount), 0) as paidAmount " +
            "from Investment i " +
            "join i.loan as l " +
            "left join l.payments as p " +
            "where i.investor.id=:investorId and l.status=com.swaperclone.entity.LoanStatus.IN_PROGRESS " +
            "group by i.id")
    Page<InvestmentStatusModel> findInvestmentStatuses(@Param("investorId") Long investorId, Pageable pageable);

}
