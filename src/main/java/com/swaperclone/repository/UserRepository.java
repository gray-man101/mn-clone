package com.swaperclone.repository;

import com.swaperclone.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
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

}
