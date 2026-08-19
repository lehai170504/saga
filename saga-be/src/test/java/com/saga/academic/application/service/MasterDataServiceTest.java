package com.saga.academic.application.service;

import com.saga.academic.application.dto.CreateCourseRequest;
import com.saga.academic.application.dto.CreateSemesterRequest;
import com.saga.academic.domain.Semester;
import com.saga.academic.infrastructure.persistence.entity.ActiveSemesterSettingEntity;
import com.saga.academic.infrastructure.persistence.entity.CourseEntity;
import com.saga.academic.infrastructure.persistence.entity.SemesterEntity;
import com.saga.academic.infrastructure.persistence.repository.JpaActiveSemesterRepository;
import com.saga.academic.infrastructure.persistence.repository.JpaCourseRepository;
import com.saga.academic.infrastructure.persistence.repository.JpaSemesterRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MasterDataServiceTest {

    @Mock
    private JpaSemesterRepository semesterRepository;
    
    @Mock
    private JpaCourseRepository courseRepository;
    
    @Mock
    private JpaActiveSemesterRepository activeSemesterRepository;

    @InjectMocks
    private MasterDataService masterDataService;

    @Test
    void createSemester_Success() {
        CreateSemesterRequest request = new CreateSemesterRequest();
        request.setName("Fall 2026");
        request.setStartDate(LocalDate.of(2026, 9, 1));
        request.setEndDate(LocalDate.of(2026, 12, 31));

        SemesterEntity savedEntity = new SemesterEntity();
        savedEntity.setId(UUID.randomUUID());
        savedEntity.setName("Fall 2026");
        savedEntity.setStartDate(request.getStartDate());
        savedEntity.setEndDate(request.getEndDate());

        when(semesterRepository.save(any(SemesterEntity.class))).thenReturn(savedEntity);

        Semester result = masterDataService.createSemester(request);

        assertEquals("Fall 2026", result.getName());
        verify(semesterRepository).save(any(SemesterEntity.class));
    }

    @Test
    void createSemester_InvalidDates_ThrowsException() {
        CreateSemesterRequest request = new CreateSemesterRequest();
        request.setName("Fall 2026");
        request.setStartDate(LocalDate.of(2026, 12, 31)); // Start after end
        request.setEndDate(LocalDate.of(2026, 9, 1));

        assertThrows(IllegalArgumentException.class, () -> masterDataService.createSemester(request));
    }

    @Test
    void deleteSemester_WithCourses_ThrowsException() {
        UUID semesterId = UUID.randomUUID();
        when(courseRepository.countBySemesterId(semesterId)).thenReturn(1L);

        assertThrows(IllegalArgumentException.class, () -> masterDataService.deleteSemester(semesterId));
    }

    @Test
    void setActiveSemester_Success() {
        UUID semesterId = UUID.randomUUID();
        when(semesterRepository.existsById(semesterId)).thenReturn(true);

        masterDataService.setActiveSemester(semesterId);

        verify(activeSemesterRepository).deleteAll();
        verify(activeSemesterRepository).save(any(ActiveSemesterSettingEntity.class));
    }

    @Test
    void assignCourseToLecturer_Success() {
        CreateCourseRequest request = new CreateCourseRequest();
        request.setSemesterId(UUID.randomUUID());
        request.setSubjectId(UUID.randomUUID());
        request.setClassId(UUID.randomUUID());
        request.setInstructorId(UUID.randomUUID());

        masterDataService.assignCourseToLecturer(request);

        verify(courseRepository).save(any(CourseEntity.class));
    }
}
