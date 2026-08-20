package com.saga.academic.infrastructure.adapter;

import com.saga.project.application.port.ProjectSecurityPort;
import com.saga.academic.infrastructure.persistence.repository.JpaTeamRepository;
import com.saga.academic.infrastructure.persistence.repository.JpaCourseRepository;
import com.saga.academic.infrastructure.persistence.repository.JpaTeamMemberRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ProjectSecurityAdapter implements ProjectSecurityPort {

    private final JpaTeamRepository teamRepository;
    private final JpaCourseRepository courseRepository;
    private final JpaTeamMemberRepository teamMemberRepository;

    public ProjectSecurityAdapter(
            JpaTeamRepository teamRepository,
            JpaCourseRepository courseRepository,
            JpaTeamMemberRepository teamMemberRepository) {
        this.teamRepository = teamRepository;
        this.courseRepository = courseRepository;
        this.teamMemberRepository = teamMemberRepository;
    }

    @Override
    public boolean isLecturerOfTeam(UUID userId, UUID teamId) {
        return teamRepository.findById(teamId)
                .flatMap(team -> courseRepository.findById(team.getCourseId()))
                .map(course -> course.getInstructorId().equals(userId))
                .orElse(false);
    }

    @Override
    public boolean isStudentInTeam(UUID userId, UUID teamId) {
        return teamMemberRepository.findByTeamIdAndStudentId(teamId, userId).isPresent();
    }
}
