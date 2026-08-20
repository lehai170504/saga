package com.saga.academic.infrastructure.persistence.repository;
import com.saga.academic.infrastructure.persistence.entity.ClassEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;
@Repository
public interface JpaClassRepository extends JpaRepository<ClassEntity, UUID> {
    boolean existsByName(String name);
}
