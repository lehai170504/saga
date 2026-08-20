package com.saga.academic.repository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.saga.academic.entity.CourseStudent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;
import java.util.Optional;

@Repository
public interface JpaCourseStudentRepository extends JpaRepository<CourseStudent, UUID>, JpaSpecificationExecutor<CourseStudent> {
    Optional<CourseStudent> findByCourseIdAndStudentId(UUID courseId, UUID studentId);
}
