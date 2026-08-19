package com.saga.project.infrastructure.persistence.repository;
import com.saga.project.infrastructure.persistence.entity.JiraBoardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;
@Repository
public interface JpaJiraBoardRepository extends JpaRepository<JiraBoardEntity, UUID> {}