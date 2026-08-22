package com.saga.academic.service;

import com.saga.academic.dto.CourseRequest;
import com.saga.academic.dto.CourseResponse;
import com.saga.academic.entity.Course;
import com.saga.academic.repository.*;
import com.saga.shared.exception.BadRequestException;
import com.saga.user.repository.JpaUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CourseServiceTest {

    @Mock
    private JpaCourseRepository courseRepository;
    @Mock
    private JpaCourseStudentRepository courseStudentRepository;
    @Mock
    private JpaSemesterRepository semesterRepository;
    @Mock
    private JpaSubjectRepository subjectRepository;
    @Mock
    private JpaClassRepository classRepository;
    @Mock
    private JpaUserRepository userRepository;

    @InjectMocks
    private CourseService courseService;

    @Test
    void createCourse_HappyPath_CreatesSuccessfully() {
        UUID semesterId = UUID.randomUUID();
        UUID subjectId = UUID.randomUUID();
        UUID classId = UUID.randomUUID();

        CourseRequest request = new CourseRequest();
        request.setSemesterId(semesterId);
        request.setSubjectId(subjectId);
        request.setClassId(classId);

        when(semesterRepository.existsById(semesterId)).thenReturn(true);
        when(subjectRepository.existsById(subjectId)).thenReturn(true);
        when(classRepository.existsById(classId)).thenReturn(true);
        when(courseRepository.existsBySemesterIdAndSubjectIdAndClassId(semesterId, subjectId, classId))
                .thenReturn(false);

        Course savedCourse = new Course();
        savedCourse.setId(UUID.randomUUID());
        savedCourse.setSemesterId(semesterId);
        savedCourse.setSubjectId(subjectId);
        savedCourse.setClassId(classId);

        when(courseRepository.save(any(Course.class))).thenReturn(savedCourse);

        CourseResponse response = courseService.createCourse(request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(savedCourse.getId());
        verify(courseRepository, times(1)).save(any(Course.class));
    }

    @Test
    void createCourse_ExceptionPath_DuplicateCombination() {
        UUID semesterId = UUID.randomUUID();
        UUID subjectId = UUID.randomUUID();
        UUID classId = UUID.randomUUID();

        CourseRequest request = new CourseRequest();
        request.setSemesterId(semesterId);
        request.setSubjectId(subjectId);
        request.setClassId(classId);

        when(semesterRepository.existsById(semesterId)).thenReturn(true);
        when(subjectRepository.existsById(subjectId)).thenReturn(true);
        when(classRepository.existsById(classId)).thenReturn(true);

        when(courseRepository.existsBySemesterIdAndSubjectIdAndClassId(semesterId, subjectId, classId))
                .thenReturn(true);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            courseService.createCourse(request);
        });

        assertThat(exception.getMessage())
                .isEqualTo("This Course combination (Semester, Subject, Class) already exists.");
        verify(courseRepository, never()).save(any(Course.class));
    }
}
