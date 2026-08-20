package com.saga.user.infrastructure.persistence.repository;

import com.saga.user.infrastructure.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.saga.user.domain.Role;
import java.util.List;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaUserRepository extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByEmail(String email);
    Page<UserEntity> findByRole(Role role, Pageable pageable);
    List<UserEntity> findByRole(Role role);
}
