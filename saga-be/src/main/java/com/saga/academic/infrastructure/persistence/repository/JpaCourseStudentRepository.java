package com.saga.academic.infrastructure.persistence.repository;

import com.saga.academic.infrastructure.persistence.entity.CourseStudentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;
import java.util.Optional;

@Repository
public interface JpaCourseStudentRepository extends JpaRepository<CourseStudentEntity, UUID> {
    Optional<CourseStudentEntity> findByCourseIdAndStudentId(UUID courseId, UUID studentId);
}
