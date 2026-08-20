package com.saga.academic.service;

import com.saga.academic.dto.CourseDTO;
import com.saga.academic.dto.SemesterDTO;
import com.saga.academic.dto.TeamDTO;
import com.saga.academic.dto.TeamDetailDTO;
import com.saga.user.dto.UserResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface AcademicQueryService {
    Page<SemesterDTO> getSemesters(Pageable pageable, String search);

    Page<CourseDTO> getCourses(Pageable pageable, String search);

    Page<CourseDTO> getCoursesByLecturer(UUID lecturerId, Pageable pageable, String search);

    Page<UserResponseDTO> getCourseStudents(UUID courseId, UUID lecturerId, Pageable pageable, String search);

    Page<TeamDTO> getCourseTeams(UUID courseId, UUID lecturerId, Pageable pageable, String search);

    Page<CourseDTO> getCoursesByStudent(UUID studentId, Pageable pageable, String search);

    TeamDetailDTO getMyTeamInCourse(UUID courseId, UUID studentId);
}
