package com.saga.auth.application.service;
import com.saga.auth.application.port.GoogleAuthPort;
import com.saga.auth.application.port.JwtProviderPort;
import com.saga.user.application.port.UserRepositoryPort;
import com.saga.user.application.port.StudentRepositoryPort;
import com.saga.user.application.port.LecturerRepositoryPort;

import com.saga.auth.application.dto.AuthResponse;
import com.saga.auth.application.dto.GoogleLoginRequest;
import com.saga.auth.application.dto.UserProfileDTO;
import com.saga.user.domain.Student;
import com.saga.user.domain.User;
import com.saga.shared.exception.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private GoogleAuthPort googleAuthPort;

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Mock
    private StudentRepositoryPort studentRepositoryPort;

    @Mock
    private LecturerRepositoryPort lecturerRepositoryPort;

    @Mock
    private JwtProviderPort jwtProviderPort;

    @InjectMocks
    private AuthService authService;

    private GoogleLoginRequest request;
    private UserProfileDTO mockProfile;

    @BeforeEach
    void setUp() {
        request = new GoogleLoginRequest();
        request.setIdToken("mock_google_token");
    }

    @Test
    void loginWithGoogle_Student_Success_WhenUserDoesNotExist() {
        // Arrange
        mockProfile = UserProfileDTO.builder()
                .email("student_se123456@fpt.edu.vn")
                .name("Student")
                .picture("pic.jpg")
                .build();

        when(googleAuthPort.verifyToken(request.getIdToken())).thenReturn(mockProfile);
        when(userRepositoryPort.findByEmail("student_se123456@fpt.edu.vn")).thenReturn(Optional.empty());

        when(userRepositoryPort.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            return u;
        });

        when(jwtProviderPort.generateToken(any(User.class))).thenReturn("mock_local_jwt");

        // Act
        AuthResponse response = authService.loginWithGoogle(request);

        // Assert
        assertNotNull(response);
        assertEquals("mock_local_jwt", response.getAccessToken());
        assertEquals("STUDENT", response.getRole());
        assertEquals("student_se123456@fpt.edu.vn", response.getUser().getEmail());

        verify(userRepositoryPort, times(1)).save(any(User.class));
        verify(studentRepositoryPort, times(1)).save(any(Student.class));
        verify(lecturerRepositoryPort, never()).save(any());
    }

    @Test
    void loginWithGoogle_ThrowsUnauthorized_WhenInvalidDomain() {
        // Arrange
        mockProfile = UserProfileDTO.builder()
                .email("hacker@gmail.com")
                .name("Hacker")
                .picture("pic.jpg")
                .build();

        when(googleAuthPort.verifyToken(request.getIdToken())).thenReturn(mockProfile);
        when(userRepositoryPort.findByEmail("hacker@gmail.com")).thenReturn(Optional.empty());

        // Act & Assert
        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> {
            authService.loginWithGoogle(request);
        });

        assertEquals("Invalid email domain.", exception.getMessage());
        verify(userRepositoryPort, never()).save(any(User.class));
        verify(studentRepositoryPort, never()).save(any(Student.class));
    }
}