package com.saga.user.application.port;

import com.saga.user.domain.User;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.saga.user.domain.Role;
import java.util.List;

public interface UserRepositoryPort {
    Optional<User> findByEmail(String email);

    Optional<User> findById(java.util.UUID id);

    User save(User user);

    Page<User> findAll(Pageable pageable);

    Page<User> findByRole(Role role, Pageable pageable);

    List<User> findByRole(Role role);
}
