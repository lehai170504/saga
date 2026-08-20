package com.saga.academic.service;

import com.saga.academic.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface AdminAcademicService {
    SubjectResponse createSubject(SubjectRequest request);

    SubjectResponse updateSubject(UUID id, SubjectRequest request);

    void deleteSubject(UUID id);

    Page<SubjectResponse> getAllSubjects(Pageable pageable, String search);

    ClassResponse createClass(ClassRequest request);

    ClassResponse updateClass(UUID id, ClassRequest request);

    void deleteClass(UUID id);

    Page<ClassResponse> getAllClasses(Pageable pageable, String search);
}
