package com.saga.academic.service;

import com.saga.academic.dto.CreateCourseRequest;
import com.saga.academic.dto.CreateSemesterRequest;
import com.saga.academic.entity.Semester;
import com.saga.academic.entity.ActiveSemesterSetting;
import com.saga.academic.entity.Course;
import com.saga.academic.repository.JpaActiveSemesterRepository;
import com.saga.academic.repository.JpaCourseRepository;
import com.saga.academic.repository.JpaSemesterRepository;
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
        Semester entity = new Semester();
        entity.setCode(request.getCode());
        entity.setName(request.getName());
        entity.setStartDate(request.getStartDate());
        entity.setEndDate(request.getEndDate());
        Semester saved = semesterRepository.save(entity);
        return Semester.builder().id(saved.getId()).code(saved.getCode()).name(saved.getName())
                .startDate(saved.getStartDate())
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
        ActiveSemesterSetting setting = new ActiveSemesterSetting();
        setting.setSemesterId(semesterId);
        activeSemesterRepository.save(setting);
    }

    @Transactional
    public void assignCourseToLecturer(CreateCourseRequest request) {
        Course entity = new Course();
        entity.setSemesterId(request.getSemesterId());
        entity.setSubjectId(request.getSubjectId());
        entity.setClassId(request.getClassId());
        entity.setInstructorId(request.getInstructorId());
        courseRepository.save(entity);
    }
}