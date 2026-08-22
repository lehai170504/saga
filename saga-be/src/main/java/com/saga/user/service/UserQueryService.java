package com.saga.user.service;

import com.saga.shared.util.SearchSpecification;
import com.saga.user.dto.UserResponseDTO;
import com.saga.user.entity.Role;
import com.saga.user.entity.User;
import com.saga.user.entity.UserStatus;
import com.saga.user.repository.JpaUserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
        Specification<User> spec = Specification.where(null);
        if (search != null && !search.trim().isEmpty()) {
            spec = spec.and(SearchSpecification.searchByFields(search, "name", "email"));
        }
        if (status != null && !status.trim().isEmpty()) {
            try {
                spec = spec.and(SearchSpecification.exactMatch("status", UserStatus.valueOf(status.toUpperCase())));
            } catch (IllegalArgumentException ignored) {
            }
        }
        if (role != null && !role.trim().isEmpty()) {
            try {
                spec = spec.and(SearchSpecification.exactMatch("role", Role.valueOf(role.toUpperCase())));
            } catch (IllegalArgumentException ignored) {
            }
        }
        return userRepository.findAll(spec, pageable).map(this::toDto);
    }

    public Page<UserResponseDTO> getLecturers(Pageable pageable, String search, String status) {
        return getAllUsers(pageable, search, status, Role.LECTURER.name());
    }

    public List<UserResponseDTO> getAllLecturers() {
        return userRepository.findByRole(Role.LECTURER).stream().map(this::toDto).collect(Collectors.toList());
    }

    public Page<UserResponseDTO> getStudents(Pageable pageable, String search, String status) {
        return getAllUsers(pageable, search, status, Role.STUDENT.name());
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
