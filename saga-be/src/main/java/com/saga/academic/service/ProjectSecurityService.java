package com.saga.academic.service;

import com.saga.academic.service.ProjectSecurityService;
import com.saga.academic.repository.JpaTeamRepository;
import com.saga.academic.repository.JpaCourseRepository;
import com.saga.academic.repository.JpaTeamMemberRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ProjectSecurityService {

    private final JpaTeamRepository teamRepository;
    private final JpaCourseRepository courseRepository;
    private final JpaTeamMemberRepository teamMemberRepository;

    public ProjectSecurityService(
            JpaTeamRepository teamRepository,
            JpaCourseRepository courseRepository,
            JpaTeamMemberRepository teamMemberRepository) {
        this.teamRepository = teamRepository;
        this.courseRepository = courseRepository;
        this.teamMemberRepository = teamMemberRepository;
    }

    public boolean isLecturerOfTeam(UUID userId, UUID teamId) {
        return teamRepository.findById(teamId)
                .flatMap(team -> courseRepository.findById(team.getCourseId()))
                .map(course -> course.getInstructorId().equals(userId))
                .orElse(false);
    }

    public boolean isStudentInTeam(UUID userId, UUID teamId) {
        return teamMemberRepository.findByTeamIdAndStudentId(teamId, userId).isPresent();
    }
}
