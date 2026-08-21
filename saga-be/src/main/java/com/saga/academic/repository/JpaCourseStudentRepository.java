package com.saga.academic.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.saga.academic.entity.Course;
import com.saga.academic.entity.CourseStudent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;
import java.util.Optional;

@Repository
public interface JpaCourseStudentRepository
        extends JpaRepository<CourseStudent, UUID>, JpaSpecificationExecutor<CourseStudent> {
    Optional<CourseStudent> findByCourseIdAndStudentId(UUID courseId, UUID studentId);

    @Query("SELECT c FROM Course c INNER JOIN CourseStudent cs ON c.id = cs.courseId WHERE cs.studentId = :studentId")
    Page<Course> findCoursesByStudentId(@Param("studentId") UUID studentId, Pageable pageable);

    @Query("SELECT COUNT(DISTINCT cs.studentId) FROM CourseStudent cs JOIN Course c ON cs.courseId = c.id WHERE c.instructorId = :instructorId")
    long countUniqueStudentsByInstructorId(@Param("instructorId") UUID instructorId);
}
