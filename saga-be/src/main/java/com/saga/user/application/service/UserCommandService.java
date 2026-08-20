package com.saga.user.application.service;

import com.saga.user.application.port.UserRepositoryPort;
import com.saga.user.domain.User;
import com.saga.user.domain.UserStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UserCommandService {
    private final UserRepositoryPort userRepositoryPort;

    public UserCommandService(UserRepositoryPort userRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
    }

    @Transactional
    public void updateUserStatus(UUID userId, UserStatus status) {
        User user = userRepositoryPort.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setStatus(status);
        userRepositoryPort.save(user);
    }
}
