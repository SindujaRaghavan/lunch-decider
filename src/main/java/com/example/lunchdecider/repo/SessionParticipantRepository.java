package com.example.lunchdecider.repo;

import com.example.lunchdecider.domain.SessionParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SessionParticipantRepository extends JpaRepository<SessionParticipant, Long> {
    boolean existsBySession_IdAndUser_Id(Long sessionId, Long userId);
    @Query("""
    select sp
    from SessionParticipant sp
    join fetch sp.user u
    where sp.session.id = :sessionId
  """)
    List<SessionParticipant> findBySessionIdWithUser(@Param("sessionId") Long sessionId);
}
