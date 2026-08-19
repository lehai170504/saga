package com.saga.academic.infrastructure.adapter;
import com.saga.project.application.port.TeamValidationPort;
import com.saga.academic.infrastructure.persistence.repository.JpaTeamMemberRepository;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class TeamValidationAdapter implements TeamValidationPort {
    private final JpaTeamMemberRepository teamMemberRepository;
    
    public TeamValidationAdapter(JpaTeamMemberRepository teamMemberRepository) {
        this.teamMemberRepository = teamMemberRepository;
    }
    
    @Override
    public boolean isLeader(UUID userId, UUID teamId) {
        return teamMemberRepository.findByTeamIdAndStudentId(teamId, userId)
            .map(member -> Boolean.TRUE.equals(member.getIsLeader()))
            .orElse(false);
    }
}