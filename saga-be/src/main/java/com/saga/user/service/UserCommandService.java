package com.saga.user.service;

import com.saga.shared.exception.BadRequestException;
import com.saga.user.repository.JpaUserRepository;
import com.saga.user.entity.User;
import com.saga.user.entity.UserStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UserCommandService {
    private final JpaUserRepository userRepository;

    public UserCommandService(JpaUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    @com.saga.shared.annotation.LogAction(actionType = "UPDATE_USER_STATUS")
    public void updateUserStatus(UUID userId, UserStatus status) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found"));
        user.setStatus(status);
        userRepository.save(user);
    }
}
