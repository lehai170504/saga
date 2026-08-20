package com.saga.user.infrastructure.adapter;

import com.saga.user.application.port.UserRepositoryPort;
import com.saga.user.domain.User;
import com.saga.user.infrastructure.persistence.entity.UserEntity;
import com.saga.user.infrastructure.persistence.repository.JpaUserRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.saga.user.domain.Role;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final JpaUserRepository jpaUserRepository;

    public UserRepositoryAdapter(JpaUserRepository jpaUserRepository) {
        this.jpaUserRepository = jpaUserRepository;
    }

    @Override
    public Optional<User> findById(java.util.UUID id) {
        return jpaUserRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaUserRepository.findByEmail(email).map(this::toDomain);
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        return jpaUserRepository.findAll(pageable).map(this::toDomain);
    }

    @Override
    public User save(User user) {
        UserEntity entity = toEntity(user);
        return toDomain(jpaUserRepository.save(entity));
    }

    @Override
    public Page<User> findByRole(Role role, Pageable pageable) {
        return jpaUserRepository.findByRole(role, pageable).map(this::toDomain);
    }

    @Override
    public List<User> findByRole(Role role) {
        return jpaUserRepository.findByRole(role).stream().map(this::toDomain).collect(Collectors.toList());
    }

    private User toDomain(UserEntity entity) {
        return User.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .name(entity.getName())
                .picture(entity.getPicture())
                .password(entity.getPassword())
                .role(entity.getRole())
                .status(entity.getStatus())
                .build();
    }

    private UserEntity toEntity(User user) {
        return UserEntity.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .picture(user.getPicture())
                .role(user.getRole())
                .status(user.getStatus())
                .build();
    }
}
