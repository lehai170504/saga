package com.saga.academic.application.service;
import com.saga.academic.application.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;
public interface AdminAcademicService {
    SubjectResponse createSubject(SubjectRequest request);
    SubjectResponse updateSubject(UUID id, SubjectRequest request);
    void deleteSubject(UUID id);
    Page<SubjectResponse> getAllSubjects(Pageable pageable);

    ClassResponse createClass(ClassRequest request);
    ClassResponse updateClass(UUID id, ClassRequest request);
    void deleteClass(UUID id);
    Page<ClassResponse> getAllClasses(Pageable pageable);
}
