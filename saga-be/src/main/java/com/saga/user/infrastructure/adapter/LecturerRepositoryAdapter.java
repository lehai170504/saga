package com.saga.user.infrastructure.adapter;

import com.saga.user.application.port.LecturerRepositoryPort;
import com.saga.user.domain.Lecturer;
import com.saga.user.infrastructure.persistence.entity.LecturerEntity;
import com.saga.user.infrastructure.persistence.repository.JpaLecturerRepository;
import org.springframework.stereotype.Component;

@Component
public class LecturerRepositoryAdapter implements LecturerRepositoryPort {

    private final JpaLecturerRepository jpaLecturerRepository;

    public LecturerRepositoryAdapter(JpaLecturerRepository jpaLecturerRepository) {
        this.jpaLecturerRepository = jpaLecturerRepository;
    }

    @Override
    public Lecturer save(Lecturer lecturer) {
        LecturerEntity entity = LecturerEntity.builder()
                .id(lecturer.getId())
                .userId(lecturer.getUserId())
                .build();
        LecturerEntity saved = jpaLecturerRepository.save(entity);
        return Lecturer.builder()
                .id(saved.getId())
                .userId(saved.getUserId())
                .build();
    }
}
