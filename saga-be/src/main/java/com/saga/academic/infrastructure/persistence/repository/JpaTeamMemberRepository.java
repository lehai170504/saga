package com.saga.academic.infrastructure.persistence.repository;
import com.saga.academic.infrastructure.persistence.entity.TeamMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface JpaTeamMemberRepository extends JpaRepository<TeamMemberEntity, UUID> {
    List<TeamMemberEntity> findByTeamId(UUID teamId);
    List<TeamMemberEntity> findByStudentId(UUID studentId);
    Page<TeamMemberEntity> findByTeamId(UUID teamId, Pageable pageable);    java.util.Optional<com.saga.academic.infrastructure.persistence.entity.TeamMemberEntity> findByTeamIdAndStudentId(UUID teamId, UUID studentId);
}