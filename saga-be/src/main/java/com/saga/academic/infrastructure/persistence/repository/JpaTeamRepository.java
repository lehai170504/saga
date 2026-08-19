package com.saga.academic.infrastructure.persistence.repository;
import com.saga.academic.infrastructure.persistence.entity.TeamEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import java.util.Optional;
public interface JpaTeamRepository extends JpaRepository<TeamEntity, UUID> {
    Optional<TeamEntity> findByNameAndCourseId(String name, UUID courseId);
}