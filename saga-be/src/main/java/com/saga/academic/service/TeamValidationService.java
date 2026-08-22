package com.saga.academic.service;

import com.saga.academic.repository.JpaTeamMemberRepository;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class TeamValidationService {
    private final JpaTeamMemberRepository teamMemberRepository;

    public TeamValidationService(JpaTeamMemberRepository teamMemberRepository) {
        this.teamMemberRepository = teamMemberRepository;
    }

    public boolean isLeader(UUID userId, UUID teamId) {
        return teamMemberRepository.findByTeamIdAndStudentId(teamId, userId)
                .map(member -> Boolean.TRUE.equals(member.getIsLeader()))
                .orElse(false);
    }
}