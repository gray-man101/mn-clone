package com.example.mnclone.repository;

import com.example.mnclone.entity.Ivst;
import com.example.mnclone.model.IvstStatusModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface IvstRepository extends JpaRepository<Ivst, Long> {

    @Query("select i from Ivst i where i.user.id=:userId order by i.ln.created desc")
    Page<Ivst> findByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("select i.id as id, l.amount as overallAmount, count(p.id) as payments, sum(p.amount) as paidAmount " +
            "from Ivst i " +
            "join i.ln as l " +
            "join l.ps as p " +
            "where i.id=:ivstId " +
            "group by i.id")
    Optional<IvstStatusModel> findIvstStatus(@Param("ivstId") Long ivstId);

}
