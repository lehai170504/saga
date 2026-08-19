package com.saga.user.application.port;

import com.saga.user.domain.User;
import java.util.Optional;

public interface UserRepositoryPort {
    Optional<User> findByEmail(String email);

    User save(User user);
}
