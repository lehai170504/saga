package com.saga.user.service;

import com.saga.user.dto.UserResponseDTO;
import com.saga.user.repository.JpaUserRepository;
import com.saga.user.entity.Role;
import com.saga.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserQueryService {
    private final JpaUserRepository userRepository;

    public UserQueryService(JpaUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Page<UserResponseDTO> getAllUsers(Pageable pageable, String search, String status, String role) {
        return userRepository.findAll(pageable).map(this::toDto);
    }

    public Page<UserResponseDTO> getLecturers(Pageable pageable, String search, String status) {
        return userRepository.findByRole(Role.LECTURER, pageable).map(this::toDto);
    }

    public List<UserResponseDTO> getAllLecturers() {
        return userRepository.findByRole(Role.LECTURER).stream().map(this::toDto).collect(Collectors.toList());
    }

    public Page<UserResponseDTO> getStudents(Pageable pageable, String search, String status) {
        return userRepository.findByRole(Role.STUDENT, pageable).map(this::toDto);
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

