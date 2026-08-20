package com.saga.academic.repository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import com.saga.academic.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;
@Repository
public interface JpaSubjectRepository extends JpaRepository<Subject, UUID>, JpaSpecificationExecutor<Subject> {
    boolean existsByCode(String code);
}
