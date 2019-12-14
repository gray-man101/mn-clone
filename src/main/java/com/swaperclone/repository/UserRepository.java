package com.swaperclone.repository;

import com.swaperclone.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select u from User u where u.registered=true and u.id=:id")
    Optional<User> findRegisteredById(@Param("id") Long id);

    @Query("select u from User u where u.email=:email and u.registered=true")
    Optional<User> findByEmail(@Param("email") String email);

    @Query("select u from User u where u.registered=false and u.registrationToken=:token")
    Optional<User> findByRegistrationToken(@Param("token") String token);

    @Query("select i.investor from Investment i where i.loan.id=:loanId")
    Optional<User> findInvestorByLoanId(@Param("loanId") Long loanId);

    @Modifying
    @Query("update User u set u.registered=true where u.registrationToken=:token")
    int markUserAsRegistered(@Param("token") String token);

}
