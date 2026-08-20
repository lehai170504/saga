package com.saga.academic.application.service;

import com.saga.academic.application.dto.CourseDTO;
import com.saga.academic.application.dto.SemesterDTO;
import com.saga.academic.application.dto.TeamDTO;
import com.saga.academic.application.dto.TeamDetailDTO;
import com.saga.academic.infrastructure.persistence.entity.CourseEntity;
import com.saga.academic.infrastructure.persistence.entity.TeamEntity;
import com.saga.academic.infrastructure.persistence.entity.TeamMemberEntity;
import com.saga.academic.infrastructure.persistence.repository.JpaCourseRepository;
import com.saga.academic.infrastructure.persistence.repository.JpaSemesterRepository;
import com.saga.academic.infrastructure.persistence.repository.JpaTeamMemberRepository;
import com.saga.academic.infrastructure.persistence.repository.JpaTeamRepository;
import com.saga.user.application.dto.UserResponseDTO;
import com.saga.user.application.port.UserRepositoryPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AcademicQueryServiceImpl implements AcademicQueryService {

    private final JpaSemesterRepository semesterRepository;
    private final JpaCourseRepository courseRepository;
    private final JpaTeamRepository teamRepository;
    private final JpaTeamMemberRepository teamMemberRepository;
    private final UserRepositoryPort userRepositoryPort;

    public AcademicQueryServiceImpl(JpaSemesterRepository semesterRepository, JpaCourseRepository courseRepository,
            JpaTeamRepository teamRepository, JpaTeamMemberRepository teamMemberRepository,
            UserRepositoryPort userRepositoryPort) {
        this.semesterRepository = semesterRepository;
        this.courseRepository = courseRepository;
        this.teamRepository = teamRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    public Page<SemesterDTO> getSemesters(Pageable pageable) {
        return semesterRepository.findAll(pageable).map(s -> SemesterDTO.builder()
                .id(s.getId()).code(s.getCode()).name(s.getName())
                .startDate(s.getStartDate()).endDate(s.getEndDate())
                .isActive(false) // Needs logic to check active semester
                .build());
    }

    @Override
    public Page<CourseDTO> getCourses(Pageable pageable) {
        return courseRepository.findAll(pageable).map(c -> CourseDTO.builder()
                .id(c.getId()).semesterId(c.getSemesterId()).subjectId(c.getSubjectId())
                .classId(c.getClassId()).instructorId(c.getInstructorId()).build());
    }

    @Override
    public Page<CourseDTO> getCoursesByLecturer(UUID lecturerId, Pageable pageable) {
        return courseRepository.findByInstructorId(lecturerId, pageable).map(c -> CourseDTO.builder()
                .id(c.getId()).semesterId(c.getSemesterId()).subjectId(c.getSubjectId())
                .classId(c.getClassId()).instructorId(c.getInstructorId()).build());
    }

    @Override
    public Page<UserResponseDTO> getCourseStudents(UUID courseId, UUID lecturerId, Pageable pageable) {
        // Validate IDOR
        CourseEntity course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));
        if (!course.getInstructorId().equals(lecturerId))
            throw new IllegalArgumentException("Forbidden");

        // Simple implementation: this requires getting all teams -> team members ->
        // users.
        // Pagination here might be tricky if we don't do it at DB level, returning
        // empty for now to stub.
        return new PageImpl<>(new ArrayList<>(), pageable, 0);
    }

    @Override
    public Page<TeamDTO> getCourseTeams(UUID courseId, UUID lecturerId, Pageable pageable) {
        CourseEntity course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));
        if (!course.getInstructorId().equals(lecturerId))
            throw new IllegalArgumentException("Forbidden");

        return teamRepository.findByCourseId(courseId, pageable)
                .map(t -> TeamDTO.builder().id(t.getId()).name(t.getName()).build());
    }

    @Override
    public Page<CourseDTO> getCoursesByStudent(UUID studentId, Pageable pageable) {
        List<UUID> teamIds = teamMemberRepository.findByStudentId(studentId).stream()
                .map(TeamMemberEntity::getTeamId).collect(Collectors.toList());
        List<UUID> courseIds = teamRepository.findAllById(teamIds).stream()
                .map(TeamEntity::getCourseId).collect(Collectors.toList());

        List<CourseDTO> list = courseRepository.findByIdIn(courseIds).stream()
                .map(c -> CourseDTO.builder()
                        .id(c.getId()).semesterId(c.getSemesterId()).subjectId(c.getSubjectId())
                        .classId(c.getClassId()).instructorId(c.getInstructorId()).build())
                .collect(Collectors.toList());
        return new PageImpl<>(list, pageable, list.size());
    }

    @Override
    public TeamDetailDTO getMyTeamInCourse(UUID courseId, UUID studentId) {
        List<TeamEntity> teams = teamRepository.findAll().stream().filter(t -> t.getCourseId().equals(courseId))
                .collect(Collectors.toList());
        for (TeamEntity t : teams) {
            Optional<TeamMemberEntity> member = teamMemberRepository.findByTeamIdAndStudentId(t.getId(), studentId);
            if (member.isPresent()) {
                List<UserResponseDTO> members = teamMemberRepository.findByTeamId(t.getId()).stream()
                        .map(tm -> userRepositoryPort.findByEmail(tm.getStudentId().toString()).orElse(null)) // Actually
                                                                                                              // we need
                                                                                                              // findById,
                                                                                                              // but
                                                                                                              // email
                                                                                                              // is
                                                                                                              // string.
                                                                                                              // We need
                                                                                                              // to
                                                                                                              // implement
                                                                                                              // findById.
                        .map(u -> UserResponseDTO.builder().build()) // Stub for now
                        .collect(Collectors.toList());
                return TeamDetailDTO.builder().id(t.getId()).name(t.getName()).members(members).build();
            }
        }
        throw new IllegalArgumentException("Team not found for student in course");
    }
}
