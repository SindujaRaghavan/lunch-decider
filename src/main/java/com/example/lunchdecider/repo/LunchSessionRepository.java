package com.example.lunchdecider.repo;

import com.example.lunchdecider.domain.LunchSession;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LunchSessionRepository extends JpaRepository<LunchSession, Long> {

    Optional<LunchSession> findByCode(String code);

    @Query("""
    select s
    from LunchSession s
    join fetch s.createdBy
    where s.code = :code
  """)
    Optional<LunchSession> findByCodeWithCreator(@Param("code") String code);
}
