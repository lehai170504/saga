package com.saga.academic.application.service;

import com.saga.academic.infrastructure.persistence.entity.ActiveSemesterSettingEntity;
import com.saga.academic.infrastructure.persistence.entity.CourseEntity;
import com.saga.academic.infrastructure.persistence.repository.JpaActiveSemesterRepository;
import com.saga.academic.infrastructure.persistence.repository.JpaCourseRepository;
import com.saga.academic.infrastructure.persistence.repository.JpaTeamMemberRepository;
import com.saga.academic.infrastructure.persistence.repository.JpaTeamRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CourseRosterServiceTest {

    @Mock
    private JpaCourseRepository courseRepository;
    
    @Mock
    private JpaActiveSemesterRepository activeSemesterRepository;
    
    @Mock
    private JpaTeamRepository teamRepository;
    
    @Mock
    private JpaTeamMemberRepository teamMemberRepository;

    @InjectMocks
    private CourseRosterService courseRosterService;

    @Test
    void downloadGroupingTemplate_UnauthorizedLecturer_ThrowsAccessDenied() {
        UUID courseId = UUID.randomUUID();
        UUID lecturerId = UUID.randomUUID();
        UUID differentLecturerId = UUID.randomUUID();

        CourseEntity course = new CourseEntity();
        course.setId(courseId);
        course.setInstructorId(differentLecturerId);

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        assertThrows(AccessDeniedException.class, () -> courseRosterService.downloadGroupingTemplate(courseId, lecturerId));
    }

    @Test
    void downloadGroupingTemplate_Authorized_ReturnsByteArray() {
        UUID courseId = UUID.randomUUID();
        UUID lecturerId = UUID.randomUUID();

        CourseEntity course = new CourseEntity();
        course.setId(courseId);
        course.setInstructorId(lecturerId);

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        byte[] template = courseRosterService.downloadGroupingTemplate(courseId, lecturerId);
        
        assertNotNull(template);
    }
}
