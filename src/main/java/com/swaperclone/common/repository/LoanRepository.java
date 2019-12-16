package com.swaperclone.common.repository;

import com.swaperclone.common.entity.Loan;
import com.swaperclone.company.model.InProgressLoanModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    @Query("select l from Loan l where l.status=com.swaperclone.common.entity.LoanStatus.NEW")
    Page<Loan> findNewLoans(Pageable pageable);

    @Query("select l from Loan l where l.id=:id and l.status=com.swaperclone.common.entity.LoanStatus.NEW")
    Optional<Loan> findNewLoan(@Param("id") Long id);

    @Query("select l.id, l.amount as amount, l.amountToReturn as amountToReturn, " +
            "  l.investorInterest as investorInterest, i.investor.id as investorId, coalesce(sum(p.amount), 0) as paidAmount " +
            "from Investment i " +
            "join i.loan l " +
            "left join l.payments p " +
            "where l.id=:id and l.status=com.swaperclone.common.entity.LoanStatus.IN_PROGRESS " +
            "group by l.id")
    Optional<InProgressLoanModel> findLoanInProgress(@Param("id") Long id);

    @Modifying
    @Query("delete from Loan l where l.id=:id and l.status=com.swaperclone.common.entity.LoanStatus.NEW")
    int deleteNewLoan(@Param("id") Long id);

    @Modifying
    @Query("update Loan l set l.status=com.swaperclone.common.entity.LoanStatus.FAILED where l.id=:id")
    int updateLoanStatusToFailed(@Param("id") Long id);
}
