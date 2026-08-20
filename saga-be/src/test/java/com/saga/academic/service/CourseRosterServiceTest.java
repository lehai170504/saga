package com.saga.academic.service;

import com.saga.academic.entity.Course;
import com.saga.academic.repository.JpaActiveSemesterRepository;
import com.saga.academic.repository.JpaCourseRepository;
import com.saga.academic.repository.JpaCourseStudentRepository;
import com.saga.academic.repository.JpaTeamMemberRepository;
import com.saga.academic.repository.JpaTeamRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class CourseRosterServiceTest {

    @Mock
    private JpaCourseRepository courseRepository;

    @Mock
    private JpaCourseStudentRepository courseStudentRepository;

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

        Course course = new Course();
        course.setId(courseId);
        course.setInstructorId(differentLecturerId);

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        assertThrows(AccessDeniedException.class,
                () -> courseRosterService.downloadGroupingTemplate(courseId, lecturerId));
    }

    @Test
    void importTeamGrouping_FileSizeExceedsLimit_ShouldThrowException() {
        org.springframework.web.multipart.MultipartFile mockFile = mock(
                org.springframework.web.multipart.MultipartFile.class);
        when(mockFile.getSize()).thenReturn(6L * 1024 * 1024); // 6MB

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> courseRosterService.importTeamGrouping(UUID.randomUUID(), UUID.randomUUID(), mockFile));
        assertEquals("File size exceeds 5MB limit", ex.getMessage());
    }

    @Test
    void downloadGroupingTemplate_Authorized_ReturnsByteArray() {
        UUID courseId = UUID.randomUUID();
        UUID lecturerId = UUID.randomUUID();

        Course course = new Course();
        course.setId(courseId);
        course.setInstructorId(lecturerId);

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        byte[] template = courseRosterService.downloadGroupingTemplate(courseId, lecturerId);

        assertNotNull(template);
    }
}



