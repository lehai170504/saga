package com.saga.academic.infrastructure.persistence.repository;
import com.saga.academic.infrastructure.persistence.entity.TeamMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
public interface JpaTeamMemberRepository extends JpaRepository<TeamMemberEntity, UUID> {}