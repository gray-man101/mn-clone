package com.example.mnclone.repository;

import com.example.mnclone.entity.Ln;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LnRepository extends JpaRepository<Ln, Long> {
}
