package com.saga.academic.service;

import com.saga.academic.dto.CourseDTO;
import com.saga.academic.dto.SemesterDTO;
import com.saga.academic.dto.TeamDTO;
import com.saga.academic.dto.TeamDetailDTO;
import com.saga.academic.entity.Course;
import com.saga.academic.entity.Team;
import com.saga.academic.entity.TeamMember;
import com.saga.academic.repository.JpaCourseRepository;
import com.saga.academic.repository.JpaSemesterRepository;
import com.saga.academic.repository.JpaTeamMemberRepository;
import com.saga.academic.repository.JpaTeamRepository;
import com.saga.user.dto.UserResponseDTO;
import com.saga.user.repository.JpaUserRepository;
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
    private final JpaUserRepository userRepository;

    public AcademicQueryServiceImpl(JpaSemesterRepository semesterRepository, JpaCourseRepository courseRepository,
            JpaTeamRepository teamRepository, JpaTeamMemberRepository teamMemberRepository,
            JpaUserRepository userRepository) {
        this.semesterRepository = semesterRepository;
        this.courseRepository = courseRepository;
        this.teamRepository = teamRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Page<SemesterDTO> getSemesters(Pageable pageable, String search) {
        return semesterRepository.findAll(pageable).map(s -> SemesterDTO.builder()
                .id(s.getId()).code(s.getCode()).name(s.getName())
                .startDate(s.getStartDate()).endDate(s.getEndDate())
                .isActive(false) // Needs logic to check active semester
                .build());
    }

    @Override
    public Page<CourseDTO> getCourses(Pageable pageable, String search) {
        return courseRepository.findAll(pageable).map(c -> CourseDTO.builder()
                .id(c.getId()).semesterId(c.getSemesterId()).subjectId(c.getSubjectId())
                .classId(c.getClassId()).instructorId(c.getInstructorId()).build());
    }

    @Override
    public Page<CourseDTO> getCoursesByLecturer(UUID lecturerId, Pageable pageable, String search) {
        return courseRepository.findByInstructorId(lecturerId, pageable).map(c -> CourseDTO.builder()
                .id(c.getId()).semesterId(c.getSemesterId()).subjectId(c.getSubjectId())
                .classId(c.getClassId()).instructorId(c.getInstructorId()).build());
    }

    @Override
    public Page<UserResponseDTO> getCourseStudents(UUID courseId, UUID lecturerId, Pageable pageable, String search) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));
        if (!course.getInstructorId().equals(lecturerId))
            throw new IllegalArgumentException("Forbidden");

        return new PageImpl<>(new ArrayList<>(), pageable, 0);
    }

    @Override
    public Page<TeamDTO> getCourseTeams(UUID courseId, UUID lecturerId, Pageable pageable, String search) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));
        if (!course.getInstructorId().equals(lecturerId))
            throw new IllegalArgumentException("Forbidden");

        return teamRepository.findByCourseId(courseId, pageable)
                .map(t -> TeamDTO.builder().id(t.getId()).name(t.getName()).build());
    }

    @Override
    public Page<CourseDTO> getCoursesByStudent(UUID studentId, Pageable pageable, String search) {
        List<UUID> teamIds = teamMemberRepository.findByStudentId(studentId).stream()
                .map(TeamMember::getTeamId).collect(Collectors.toList());
        List<UUID> courseIds = teamRepository.findAllById(teamIds).stream()
                .map(Team::getCourseId).collect(Collectors.toList());

        List<CourseDTO> list = courseRepository.findByIdIn(courseIds).stream()
                .map(c -> CourseDTO.builder()
                        .id(c.getId()).semesterId(c.getSemesterId()).subjectId(c.getSubjectId())
                        .classId(c.getClassId()).instructorId(c.getInstructorId()).build())
                .collect(Collectors.toList());
        return new PageImpl<>(list, pageable, list.size());
    }

    @Override
    public TeamDetailDTO getMyTeamInCourse(UUID courseId, UUID studentId) {
        List<Team> teams = teamRepository.findAll().stream().filter(t -> t.getCourseId().equals(courseId))
                .collect(Collectors.toList());
        for (Team t : teams) {
            Optional<TeamMember> member = teamMemberRepository.findByTeamIdAndStudentId(t.getId(), studentId);
            if (member.isPresent()) {
                List<UserResponseDTO> members = teamMemberRepository.findByTeamId(t.getId()).stream()
                        .map(tm -> userRepository.findByEmail(tm.getStudentId().toString()).orElse(null)) // Actually
                        .map(u -> UserResponseDTO.builder().build()) // Stub for now
                        .collect(Collectors.toList());
                return TeamDetailDTO.builder().id(t.getId()).name(t.getName()).members(members).build();
            }
        }
        throw new IllegalArgumentException("Team not found for student in course");
    }
}
