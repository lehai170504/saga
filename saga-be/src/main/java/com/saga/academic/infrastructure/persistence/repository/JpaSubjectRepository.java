package com.saga.academic.infrastructure.persistence.repository;
import com.saga.academic.infrastructure.persistence.entity.SubjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;
@Repository
public interface JpaSubjectRepository extends JpaRepository<SubjectEntity, UUID> {
    boolean existsByCode(String code);
}
