package com.saga.user.infrastructure.persistence.repository;

import com.saga.user.infrastructure.persistence.entity.StudentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface JpaStudentRepository extends JpaRepository<StudentEntity, UUID> {
}
