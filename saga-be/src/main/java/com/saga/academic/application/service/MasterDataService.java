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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
public class MasterDataService {
    private final JpaSemesterRepository semesterRepository;
    private final JpaCourseRepository courseRepository;
    private final JpaActiveSemesterRepository activeSemesterRepository;

    public MasterDataService(JpaSemesterRepository semesterRepository, JpaCourseRepository courseRepository,
            JpaActiveSemesterRepository activeSemesterRepository) {
        this.semesterRepository = semesterRepository;
        this.courseRepository = courseRepository;
        this.activeSemesterRepository = activeSemesterRepository;
    }

    @Transactional
    public Semester createSemester(CreateSemesterRequest request) {
        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new IllegalArgumentException("Start date must be before end date");
        }
        SemesterEntity entity = new SemesterEntity();
        entity.setName(request.getName());
        entity.setStartDate(request.getStartDate());
        entity.setEndDate(request.getEndDate());
        SemesterEntity saved = semesterRepository.save(entity);
        return Semester.builder().id(saved.getId()).name(saved.getName()).startDate(saved.getStartDate())
                .endDate(saved.getEndDate()).build();
    }

    @Transactional
    public void deleteSemester(UUID semesterId) {
        if (courseRepository.countBySemesterId(semesterId) > 0) {
            throw new IllegalArgumentException("Cannot delete semester that contains courses");
        }
        // Also remove from active semester if it is the active one
        activeSemesterRepository.findAll().stream()
                .filter(setting -> setting.getSemesterId().equals(semesterId))
                .forEach(activeSemesterRepository::delete);
        
        semesterRepository.deleteById(semesterId);
    }

    @Transactional
    public void setActiveSemester(UUID semesterId) {
        if (!semesterRepository.existsById(semesterId)) {
            throw new IllegalArgumentException("Semester does not exist");
        }
        activeSemesterRepository.deleteAll();
        ActiveSemesterSettingEntity setting = new ActiveSemesterSettingEntity();
        setting.setSemesterId(semesterId);
        activeSemesterRepository.save(setting);
    }

    @Transactional
    public void assignCourseToLecturer(CreateCourseRequest request) {
        CourseEntity entity = new CourseEntity();
        entity.setSemesterId(request.getSemesterId());
        entity.setSubjectId(request.getSubjectId());
        entity.setClassId(request.getClassId());
        entity.setInstructorId(request.getInstructorId());
        courseRepository.save(entity);
    }
}