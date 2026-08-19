package com.saga.application.port;

import com.saga.domain.User;
import java.util.Optional;

public interface UserRepositoryPort {
    Optional<User> findByEmail(String email);

    User save(User user);
}
