package com.saga.academic.repository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import com.saga.academic.entity.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface JpaTeamMemberRepository extends JpaRepository<TeamMember, UUID>, JpaSpecificationExecutor<TeamMember> {
    List<TeamMember> findByTeamId(UUID teamId);
    List<TeamMember> findByStudentId(UUID studentId);
    Page<TeamMember> findByTeamId(UUID teamId, Pageable pageable);    java.util.Optional<TeamMember> findByTeamIdAndStudentId(UUID teamId, UUID studentId);
}