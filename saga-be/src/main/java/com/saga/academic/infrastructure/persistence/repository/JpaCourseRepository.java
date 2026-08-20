package com.saga.academic.infrastructure.persistence.repository;
import com.saga.academic.infrastructure.persistence.entity.CourseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface JpaCourseRepository extends JpaRepository<CourseEntity, UUID> {
    Page<CourseEntity> findByInstructorId(UUID instructorId, Pageable pageable);
    java.util.List<CourseEntity> findByIdIn(java.util.List<UUID> ids);
    long countBySemesterId(UUID semesterId);
    boolean existsBySubjectId(UUID subjectId);
    boolean existsByClassId(UUID classId);
}