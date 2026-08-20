package com.saga.user.application.service;

import com.saga.user.application.dto.UserResponseDTO;
import com.saga.user.application.port.UserRepositoryPort;
import com.saga.user.domain.Role;
import com.saga.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserQueryService {
    private final UserRepositoryPort userRepositoryPort;

    public UserQueryService(UserRepositoryPort userRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
    }

    
    public Page<UserResponseDTO> getAllUsers(Pageable pageable) {
        return userRepositoryPort.findAll(pageable).map(this::toDto);
    }

    public Page<UserResponseDTO> getLecturers(Pageable pageable) {
        return userRepositoryPort.findByRole(Role.LECTURER, pageable).map(this::toDto);
    }

    public List<UserResponseDTO> getAllLecturers() {
        return userRepositoryPort.findByRole(Role.LECTURER).stream().map(this::toDto).collect(Collectors.toList());
    }

    public Page<UserResponseDTO> getStudents(Pageable pageable) {
        return userRepositoryPort.findByRole(Role.STUDENT, pageable).map(this::toDto);
    }

    private UserResponseDTO toDto(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .picture(user.getPicture())
                .role(user.getRole() != null ? user.getRole().name() : null)
                .status(user.getStatus() != null ? user.getStatus().name() : null)
                .build();
    }
}
