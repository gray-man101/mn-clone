package com.example.mnclone.repository;

import com.example.mnclone.entity.Pm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PmRepository extends JpaRepository<Pm, Long> {
}
