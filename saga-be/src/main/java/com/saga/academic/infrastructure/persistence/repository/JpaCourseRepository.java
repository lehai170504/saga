package com.saga.academic.infrastructure.persistence.repository;
import com.saga.academic.infrastructure.persistence.entity.CourseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
public interface JpaCourseRepository extends JpaRepository<CourseEntity, UUID> {
    long countBySemesterId(UUID semesterId);
}