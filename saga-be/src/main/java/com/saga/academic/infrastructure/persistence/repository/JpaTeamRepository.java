package com.saga.academic.infrastructure.persistence.repository;
import com.saga.academic.infrastructure.persistence.entity.TeamEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
public interface JpaTeamRepository extends JpaRepository<TeamEntity, UUID> {
    Page<TeamEntity> findByCourseId(UUID courseId, Pageable pageable);
    Optional<TeamEntity> findByNameAndCourseId(String name, UUID courseId);
}