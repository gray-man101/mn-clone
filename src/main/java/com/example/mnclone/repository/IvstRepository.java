package com.example.mnclone.repository;

import com.example.mnclone.entity.Ivst;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IvstRepository extends JpaRepository<Ivst, Long> {

    @Query("select i from Ivst i where i.user.id=:userId order by i.ln.created desc")
    Page<Ivst> findByUserId(@Param("userId") Long userId, Pageable pageable);

}
