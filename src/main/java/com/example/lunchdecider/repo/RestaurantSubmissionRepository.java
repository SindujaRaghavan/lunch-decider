package com.example.lunchdecider.repo;

import com.example.lunchdecider.domain.RestaurantSubmission;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RestaurantSubmissionRepository extends JpaRepository<RestaurantSubmission, Long> {

    long countBySession_Id(Long sessionId);

    @Query("""
    select rs
    from RestaurantSubmission rs
    join fetch rs.user u
    where rs.session.id = :sessionId
    order by rs.createdAt asc
  """)
    List<RestaurantSubmission> findBySessionIdWithUserOrderByCreatedAtAsc(@Param("sessionId") Long sessionId);
}
