package com.saga.user.repository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.saga.user.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface JpaStudentRepository extends JpaRepository<Student, UUID>, JpaSpecificationExecutor<Student> {
}
