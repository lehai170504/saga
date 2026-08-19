package com.saga.academic.infrastructure.persistence.repository;
import com.saga.academic.infrastructure.persistence.entity.SemesterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
public interface JpaSemesterRepository extends JpaRepository<SemesterEntity, UUID> {}