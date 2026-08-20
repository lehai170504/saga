package com.saga.academic.application.service;

import com.saga.academic.application.dto.*;
import com.saga.academic.infrastructure.persistence.entity.ClassEntity;
import com.saga.academic.infrastructure.persistence.entity.SubjectEntity;
import com.saga.academic.infrastructure.persistence.repository.JpaClassRepository;
import com.saga.academic.infrastructure.persistence.repository.JpaSubjectRepository;
import com.saga.academic.infrastructure.persistence.repository.JpaCourseRepository;
import com.saga.shared.exception.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AdminAcademicServiceImpl implements AdminAcademicService {

    private final JpaSubjectRepository subjectRepository;
    private final JpaClassRepository classRepository;
    private final JpaCourseRepository courseRepository;

    public AdminAcademicServiceImpl(JpaSubjectRepository subjectRepository, JpaClassRepository classRepository, JpaCourseRepository courseRepository) {
        this.subjectRepository = subjectRepository;
        this.classRepository = classRepository;
        this.courseRepository = courseRepository;
    }

    @Override
    @Transactional
    public SubjectResponse createSubject(SubjectRequest request) {
        if (subjectRepository.existsByCode(request.getSubjectCode())) {
            throw new BadRequestException("Subject code already exists.");
        }
        SubjectEntity entity = new SubjectEntity();
        entity.setCode(request.getSubjectCode());
        entity.setName(request.getSubjectName());
        entity = subjectRepository.save(entity);
        return mapToResponse(entity);
    }

    @Override
    @Transactional
    public SubjectResponse updateSubject(UUID id, SubjectRequest request) {
        SubjectEntity entity = subjectRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Subject not found."));
        if (!entity.getCode().equals(request.getSubjectCode()) && subjectRepository.existsByCode(request.getSubjectCode())) {
            throw new BadRequestException("Subject code already exists.");
        }
        entity.setCode(request.getSubjectCode());
        entity.setName(request.getSubjectName());
        entity = subjectRepository.save(entity);
        return mapToResponse(entity);
    }

    @Override
    @Transactional
    public void deleteSubject(UUID id) {
        if (courseRepository.existsBySubjectId(id)) {
            throw new BadRequestException("Cannot delete subject: it is being used in one or more courses.");
        }
        subjectRepository.deleteById(id);
    }

    @Override
    public Page<SubjectResponse> getAllSubjects(Pageable pageable) {
        return subjectRepository.findAll(pageable).map(this::mapToResponse);
    }

    @Override
    @Transactional
    public ClassResponse createClass(ClassRequest request) {
        if (classRepository.existsByName(request.getClassCode())) {
            throw new BadRequestException("Class code already exists.");
        }
        ClassEntity entity = new ClassEntity();
        entity.setName(request.getClassCode());
        entity = classRepository.save(entity);
        return mapToResponse(entity);
    }

    @Override
    @Transactional
    public ClassResponse updateClass(UUID id, ClassRequest request) {
        ClassEntity entity = classRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Class not found."));
        if (!entity.getName().equals(request.getClassCode()) && classRepository.existsByName(request.getClassCode())) {
            throw new BadRequestException("Class code already exists.");
        }
        entity.setName(request.getClassCode());
        entity = classRepository.save(entity);
        return mapToResponse(entity);
    }

    @Override
    @Transactional
    public void deleteClass(UUID id) {
        if (courseRepository.existsByClassId(id)) {
            throw new BadRequestException("Cannot delete class: it is being used in one or more courses.");
        }
        classRepository.deleteById(id);
    }

    @Override
    public Page<ClassResponse> getAllClasses(Pageable pageable) {
        return classRepository.findAll(pageable).map(this::mapToResponse);
    }

    private SubjectResponse mapToResponse(SubjectEntity entity) {
        return SubjectResponse.builder()
                .id(entity.getId())
                .subjectCode(entity.getCode())
                .subjectName(entity.getName())
                .build();
    }

    private ClassResponse mapToResponse(ClassEntity entity) {
        return ClassResponse.builder()
                .id(entity.getId())
                .classCode(entity.getName())
                .build();
    }
}
