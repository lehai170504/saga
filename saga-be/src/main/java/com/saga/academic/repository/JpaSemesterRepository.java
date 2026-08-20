package com.saga.academic.repository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import com.saga.academic.entity.Semester;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
public interface JpaSemesterRepository extends JpaRepository<Semester, UUID>, JpaSpecificationExecutor<Semester> {}