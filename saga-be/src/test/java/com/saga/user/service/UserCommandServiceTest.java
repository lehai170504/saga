package com.saga.user.service;

import com.saga.shared.exception.BadRequestException;
import com.saga.user.entity.User;
import com.saga.user.entity.UserStatus;
import com.saga.user.repository.JpaUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserCommandServiceTest {

    @Mock
    private JpaUserRepository userRepository;

    @InjectMocks
    private UserCommandService userCommandService;

    private User testUser;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        testUser = new User();
        testUser.setId(userId);
        testUser.setStatus(UserStatus.ACTIVE);
    }

    @Test
    void updateUserStatus_Success() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        userCommandService.updateUserStatus(userId, UserStatus.INACTIVE);

        assertEquals(UserStatus.INACTIVE, testUser.getStatus());
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void updateUserStatus_UserNotFound_ThrowsBadRequestException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> userCommandService.updateUserStatus(userId, UserStatus.INACTIVE));
        verify(userRepository, never()).save(any());
    }
}
