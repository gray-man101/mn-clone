package com.example.mnclone.repository;

import com.example.mnclone.entity.Pm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface PmRepository extends JpaRepository<Pm, Long> {

    Page<Pm> findByLnId(Long lnId, Pageable pageable);

    @Transactional
    void deleteByLnIdAndId(Long lnId, Long id);
}
