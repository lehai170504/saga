package com.saga.academic.repository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import com.saga.academic.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
public interface JpaTeamRepository extends JpaRepository<Team, UUID>, JpaSpecificationExecutor<Team> {
    Page<Team> findByCourseId(UUID courseId, Pageable pageable);
    Optional<Team> findByNameAndCourseId(String name, UUID courseId);
}