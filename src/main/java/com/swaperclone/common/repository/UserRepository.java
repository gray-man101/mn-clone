package com.swaperclone.common.repository;

import com.swaperclone.common.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select u from User u where u.registered=true and u.id=:id")
    Optional<User> findRegisteredById(@Param("id") Long id);

    @Query("select u from User u where u.email=:email and u.registered=true")
    Optional<User> findRegisteredByEmail(@Param("email") String email);

    @Modifying
    @Query("update User u set u.registered=true where u.registrationToken=:token and u.registered=false")
    int markUserAsRegistered(@Param("token") String token);

}
