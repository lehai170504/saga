package com.saga.academic.repository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import com.saga.academic.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface JpaCourseRepository extends JpaRepository<Course, UUID>, JpaSpecificationExecutor<Course> {
    Page<Course> findByInstructorId(UUID instructorId, Pageable pageable);
    java.util.List<Course> findByIdIn(java.util.List<UUID> ids);
    long countBySemesterId(UUID semesterId);
    boolean existsBySubjectId(UUID subjectId);
    boolean existsByClassId(UUID classId);
}