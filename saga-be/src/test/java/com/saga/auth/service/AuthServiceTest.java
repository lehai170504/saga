package com.saga.auth.service;

import com.saga.user.repository.JpaUserRepository;
import com.saga.user.repository.JpaStudentRepository;
import com.saga.user.repository.JpaLecturerRepository;

import com.saga.auth.dto.AuthResponse;
import com.saga.auth.dto.GoogleLoginRequest;
import com.saga.auth.dto.UserProfileDTO;
import com.saga.user.entity.Student;
import com.saga.user.entity.User;
import com.saga.shared.exception.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.List;
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
    private GoogleAuthService googleAuthPort;

    @Mock
    private JpaUserRepository userRepository;

    @Mock
    private JpaStudentRepository studentRepository;

    @Mock
    private JpaLecturerRepository lecturerRepository;

    @Mock
    private JwtProviderService jwtProviderPort;

    @Mock
    private com.saga.project.service.SystemAuditLogService auditLogService;

    @InjectMocks
    private AuthService authService;

    private GoogleLoginRequest request;
    private UserProfileDTO mockProfile;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authService, "adminEmails", List.of("admin@saga.com"));
        ReflectionTestUtils.setField(authService, "lecturerEmails", List.of("lecturer@saga.com"));
        request = new GoogleLoginRequest();
        request.setIdToken("mock_google_token");
    }

    @Test
    void loginWithGoogle_Student_Success_WhenUserDoesNotExist() {
        mockProfile = UserProfileDTO.builder()
                .email("student_se123456@fpt.edu.vn")
                .name("Student")
                .picture("pic.jpg")
                .build();

        when(googleAuthPort.verifyToken(request.getIdToken())).thenReturn(mockProfile);
        when(userRepository.findByEmail("student_se123456@fpt.edu.vn")).thenReturn(Optional.empty());

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            return u;
        });

        when(jwtProviderPort.generateToken(any(User.class))).thenReturn("mock_local_jwt");

        AuthResponse response = authService.loginWithGoogle(request);

        assertNotNull(response);
        assertEquals("mock_local_jwt", response.getAccessToken());
        assertEquals("STUDENT", response.getRole());
        assertEquals("student_se123456@fpt.edu.vn", response.getUser().getEmail());

        verify(userRepository, times(1)).save(any(User.class));
        verify(studentRepository, times(1)).save(any(Student.class));
        verify(lecturerRepository, never()).save(any());
    }

    @Test
    void loginWithGoogle_ThrowsUnauthorized_WhenInvalidDomain() {
        mockProfile = UserProfileDTO.builder()
                .email("hacker@yahoo.com")
                .name("Hacker")
                .picture("pic.jpg")
                .build();

        when(googleAuthPort.verifyToken(request.getIdToken())).thenReturn(mockProfile);
        when(userRepository.findByEmail("hacker@yahoo.com")).thenReturn(Optional.empty());

        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> {
            authService.loginWithGoogle(request);
        });

        assertEquals("Invalid email domain or not whitelisted.", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
        verify(studentRepository, never()).save(any(Student.class));
    }
}



