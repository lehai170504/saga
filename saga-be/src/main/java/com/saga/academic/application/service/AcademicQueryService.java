package com.saga.academic.application.service;

import com.saga.academic.application.dto.CourseDTO;
import com.saga.academic.application.dto.SemesterDTO;
import com.saga.academic.application.dto.TeamDTO;
import com.saga.academic.application.dto.TeamDetailDTO;
import com.saga.user.application.dto.UserResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface AcademicQueryService {
    Page<SemesterDTO> getSemesters(Pageable pageable);
    Page<CourseDTO> getCourses(Pageable pageable);
    
    Page<CourseDTO> getCoursesByLecturer(UUID lecturerId, Pageable pageable);
    Page<UserResponseDTO> getCourseStudents(UUID courseId, UUID lecturerId, Pageable pageable);
    Page<TeamDTO> getCourseTeams(UUID courseId, UUID lecturerId, Pageable pageable);
    
    Page<CourseDTO> getCoursesByStudent(UUID studentId, Pageable pageable);
    TeamDetailDTO getMyTeamInCourse(UUID courseId, UUID studentId);
}
