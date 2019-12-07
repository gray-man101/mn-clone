package com.example.mnclone.repository;

import com.example.mnclone.entity.Ln;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LnRepository extends JpaRepository<Ln, Long> {
    @Query("select l from Ln l where l.status=com.example.mnclone.entity.LnStatus.NEW")
    Page<Ln> findNewLs(Pageable pageable);

    @Query("select l from Ln l where l.id=:id and l.status=com.example.mnclone.entity.LnStatus.NEW")
    Optional<Ln> findNewById(@Param("id") Long id);
}
