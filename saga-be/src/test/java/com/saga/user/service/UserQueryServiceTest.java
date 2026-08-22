package com.saga.user.service;

import com.saga.user.dto.UserResponseDTO;
import com.saga.user.entity.Role;
import com.saga.user.entity.User;
import com.saga.user.entity.UserStatus;
import com.saga.user.repository.JpaUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.ArgumentMatchers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserQueryServiceTest {

    @Mock
    private JpaUserRepository userRepository;

    @InjectMocks
    private UserQueryService userQueryService;

    private User student;
    private User lecturer;

    @BeforeEach
    void setUp() {
        student = User.builder()
                .id(UUID.randomUUID())
                .email("student@fpt.edu.vn")
                .name("Student A")
                .role(Role.STUDENT)
                .status(UserStatus.ACTIVE)
                .build();

        lecturer = User.builder()
                .id(UUID.randomUUID())
                .email("lecturer@fpt.edu.vn")
                .name("Lecturer A")
                .role(Role.LECTURER)
                .status(UserStatus.ACTIVE)
                .build();
    }

    @Test
    void getAllUsers_ReturnsPageOfUsers() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> userPage = new PageImpl<>(List.of(student, lecturer));

        when(userRepository.findAll(ArgumentMatchers.<Specification<User>>any(), eq(pageable))).thenReturn(userPage);

        Page<UserResponseDTO> result = userQueryService.getAllUsers(pageable, "A", "ACTIVE", "STUDENT");

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals("Student A", result.getContent().get(0).getName());
        verify(userRepository, times(1)).findAll(ArgumentMatchers.<Specification<User>>any(), eq(pageable));
    }

    @Test
    void getLecturers_ReturnsOnlyLecturers() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> userPage = new PageImpl<>(List.of(lecturer));

        when(userRepository.findAll(ArgumentMatchers.<Specification<User>>any(), eq(pageable))).thenReturn(userPage);

        Page<UserResponseDTO> result = userQueryService.getLecturers(pageable, null, null);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(Role.LECTURER.name(), result.getContent().get(0).getRole());
    }

    @Test
    void getAllLecturers_ReturnsListOfLecturers() {
        when(userRepository.findByRole(Role.LECTURER)).thenReturn(List.of(lecturer));

        List<UserResponseDTO> result = userQueryService.getAllLecturers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("lecturer@fpt.edu.vn", result.get(0).getEmail());
        verify(userRepository, times(1)).findByRole(Role.LECTURER);
    }

    @Test
    void getStudents_ReturnsOnlyStudents() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> userPage = new PageImpl<>(List.of(student));

        when(userRepository.findAll(ArgumentMatchers.<Specification<User>>any(), eq(pageable))).thenReturn(userPage);

        Page<UserResponseDTO> result = userQueryService.getStudents(pageable, null, null);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(Role.STUDENT.name(), result.getContent().get(0).getRole());
    }
}
