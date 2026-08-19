package com.saga.infrastructure.adapter;

import com.saga.application.port.StudentRepositoryPort;
import com.saga.domain.Student;
import com.saga.infrastructure.persistence.entity.StudentEntity;
import com.saga.infrastructure.persistence.repository.JpaStudentRepository;
import org.springframework.stereotype.Component;

@Component
public class StudentRepositoryAdapter implements StudentRepositoryPort {

    private final JpaStudentRepository jpaStudentRepository;

    public StudentRepositoryAdapter(JpaStudentRepository jpaStudentRepository) {
        this.jpaStudentRepository = jpaStudentRepository;
    }

    @Override
    public Student save(Student student) {
        StudentEntity entity = StudentEntity.builder()
                .id(student.getId())
                .userId(student.getUserId())
                .studentCode(student.getStudentCode())
                .build();
        StudentEntity saved = jpaStudentRepository.save(entity);
        return Student.builder()
                .id(saved.getId())
                .userId(saved.getUserId())
                .studentCode(saved.getStudentCode())
                .build();
    }
}
