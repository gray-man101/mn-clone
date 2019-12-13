package com.example.mnclone.repository;

import com.example.mnclone.entity.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    @Query("select l from Loan l where l.status=com.example.mnclone.entity.LoanStatus.NEW")
    Page<Loan> findNewLoans(Pageable pageable);

    @Query("select l from Loan l where l.id=:id and l.status=com.example.mnclone.entity.LoanStatus.NEW")
    Optional<Loan> findNewLoanById(@Param("id") Long id);
}
