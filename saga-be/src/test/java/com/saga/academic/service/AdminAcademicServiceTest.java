package com.saga.academic.service;

import com.saga.academic.dto.SubjectRequest;
import com.saga.academic.dto.SubjectResponse;
import com.saga.academic.entity.Subject;
import com.saga.academic.repository.JpaClassRepository;
import com.saga.academic.repository.JpaCourseRepository;
import com.saga.academic.repository.JpaSubjectRepository;
import com.saga.shared.exception.BadRequestException;
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
public class AdminAcademicServiceTest {

    @Mock
    private JpaSubjectRepository subjectRepository;

    @Mock
    private JpaClassRepository classRepository;

    @Mock
    private JpaCourseRepository courseRepository;

    @InjectMocks
    private AdminAcademicServiceImpl adminAcademicService;

    @Test
    public void createSubject_HappyPath_ReturnsSubjectResponse() {
        // Arrange
        SubjectRequest request = new SubjectRequest();
        request.setSubjectCode("SWE301");
        request.setSubjectName("Software Engineering");

        UUID generatedId = UUID.randomUUID();
        Subject savedSubject = Subject.builder()
                .id(generatedId)
                .subjectCode("SWE301")
                .subjectName("Software Engineering")
                .build();

        when(subjectRepository.existsBySubjectCode("SWE301")).thenReturn(false);
        when(subjectRepository.save(any(Subject.class))).thenReturn(savedSubject);

        // Act
        SubjectResponse response = adminAcademicService.createSubject(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(generatedId);
        assertThat(response.getSubjectCode()).isEqualTo("SWE301");
        assertThat(response.getSubjectName()).isEqualTo("Software Engineering");

        verify(subjectRepository, times(1)).existsBySubjectCode("SWE301");
        verify(subjectRepository, times(1)).save(any(Subject.class));
    }

    @Test
    public void createSubject_DuplicateCode_ThrowsBadRequestException() {
        // Arrange
        SubjectRequest request = new SubjectRequest();
        request.setSubjectCode("SWE301");
        request.setSubjectName("Software Engineering");

        when(subjectRepository.existsBySubjectCode("SWE301")).thenReturn(true);

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> adminAcademicService.createSubject(request));

        assertThat(exception.getMessage()).isEqualTo("Subject code already exists.");

        verify(subjectRepository, times(1)).existsBySubjectCode("SWE301");
        verify(subjectRepository, never()).save(any(Subject.class)); // Ensure save is never called
    }
}
