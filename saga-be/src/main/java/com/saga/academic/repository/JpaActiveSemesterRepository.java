package com.saga.academic.repository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import com.saga.academic.entity.ActiveSemesterSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
public interface JpaActiveSemesterRepository extends JpaRepository<ActiveSemesterSetting, UUID>, JpaSpecificationExecutor<ActiveSemesterSetting> {}