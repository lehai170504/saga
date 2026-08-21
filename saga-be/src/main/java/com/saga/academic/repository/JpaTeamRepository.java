package com.saga.academic.repository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import com.saga.academic.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JpaTeamRepository extends JpaRepository<Team, UUID>, JpaSpecificationExecutor<Team> {
    Page<Team> findByCourseId(UUID courseId, Pageable pageable);
    Optional<Team> findByNameAndCourseId(String name, UUID courseId);
    
    @Query("SELECT COUNT(t) FROM Team t JOIN Course c ON t.courseId = c.id WHERE c.instructorId = :instructorId")
    long countBySupervisorId(@Param("instructorId") UUID instructorId);
}