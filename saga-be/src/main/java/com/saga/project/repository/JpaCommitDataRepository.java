package com.saga.project.repository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import com.saga.project.entity.CommitData;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;
public interface JpaCommitDataRepository extends JpaRepository<CommitData, UUID>, JpaSpecificationExecutor<CommitData> {
    Optional<CommitData> findByHash(String hash);

    long countByRepoId(UUID repoId);
    long countByAuthorEmail(String authorEmail);
    @org.springframework.data.jpa.repository.Query("SELECT cd.commitDate FROM CommitData cd WHERE cd.authorEmail IN (SELECT u.email FROM com.saga.user.entity.User u JOIN com.saga.academic.entity.CourseStudent cs ON u.id = cs.studentId JOIN com.saga.academic.entity.Course c ON cs.courseId = c.id WHERE c.instructorId = :instructorId) AND cd.commitDate IS NOT NULL")
    java.util.List<java.time.LocalDateTime> findCommitDatesByInstructorId(@org.springframework.data.repository.query.Param("instructorId") UUID instructorId);
    java.util.List<CommitData> findByAuthorEmailIn(java.util.List<String> emails);
    org.springframework.data.domain.Page<CommitData> findByRepoId(UUID repoId, org.springframework.data.domain.Pageable pageable);
}