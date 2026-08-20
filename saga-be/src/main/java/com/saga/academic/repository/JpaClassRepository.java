package com.saga.academic.repository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import com.saga.academic.entity.Class;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;
@Repository
public interface JpaClassRepository extends JpaRepository<Class, UUID>, JpaSpecificationExecutor<Class> {
    boolean existsByName(String name);
}
