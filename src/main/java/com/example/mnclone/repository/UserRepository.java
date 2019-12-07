package com.example.mnclone.repository;

import com.example.mnclone.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select u from User u where u.registered=true")
    Optional<User> findRegisteredById(Long id);

}
