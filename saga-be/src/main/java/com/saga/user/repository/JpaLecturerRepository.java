package com.saga.user.repository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.saga.user.entity.Lecturer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface JpaLecturerRepository extends JpaRepository<Lecturer, UUID>, JpaSpecificationExecutor<Lecturer> {
}
