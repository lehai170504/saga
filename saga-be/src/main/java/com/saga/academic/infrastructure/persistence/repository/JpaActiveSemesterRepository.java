package com.saga.academic.infrastructure.persistence.repository;
import com.saga.academic.infrastructure.persistence.entity.ActiveSemesterSettingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
public interface JpaActiveSemesterRepository extends JpaRepository<ActiveSemesterSettingEntity, UUID> {}