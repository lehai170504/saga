package com.saga.user.controller;

import com.saga.shared.response.ApiResponse;
import com.saga.user.dto.UserResponseDTO;
import com.saga.user.service.UserQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import com.saga.user.service.UserCommandService;
import com.saga.user.entity.UserStatus;
import java.util.UUID;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/users")
@Tag(name = "3. Admin - User APIs", description = "Admin User Management (e.g., getting lists of lecturers)")
public class AdminUserController {

    @GetMapping("/students")
    @Operation(summary = "Get Students (Paginated)")
    public ResponseEntity<ApiResponse<Page<UserResponseDTO>>> getStudents(Pageable pageable, @RequestParam(required = false) String search, @RequestParam(required = false) String status) {
        return ResponseEntity.ok(ApiResponse.success(userQueryService.getStudents(pageable, search, status), "Success"));
    }

    private final UserQueryService userQueryService;
    private final UserCommandService userCommandService;

    public AdminUserController(UserQueryService userQueryService, UserCommandService userCommandService) {
        this.userQueryService = userQueryService;
        this.userCommandService = userCommandService;
    }

    @GetMapping("/lecturers")
    @Operation(summary = "Get Lecturers (Paginated)", description = "Lấy danh sách giảng viên có phân trang (dùng cho table).")
    public ResponseEntity<ApiResponse<Page<UserResponseDTO>>> getLecturers(Pageable pageable, @RequestParam(required = false) String search, @RequestParam(required = false) String status) {
        return ResponseEntity.ok(ApiResponse.success(
                userQueryService.getLecturers(pageable, search, status),
                "Lấy danh sách giảng viên thành công"));
    }

    @GetMapping("/lecturers/all")
    @Operation(summary = "Get All Lecturers (Dropdown)", description = "Lấy toàn bộ danh sách giảng viên không phân trang (dùng cho dropdown).")
    public ResponseEntity<ApiResponse<List<UserResponseDTO>>> getAllLecturers() {
        return ResponseEntity.ok(ApiResponse.success(
                userQueryService.getAllLecturers(),
                "Lấy danh sách giảng viên thành công"));
    }

    @GetMapping
    @Operation(summary = "Get All Users (Paginated)", description = "Ly danh sAch tA?t cA? ngAEAE!i dA1ng.")
    public ResponseEntity<ApiResponse<Page<UserResponseDTO>>> getAllUsers(Pageable pageable, @RequestParam(required = false) String search, @RequestParam(required = false) String status, @RequestParam(required = false) String role) {
        return ResponseEntity.ok(ApiResponse.success(userQueryService.getAllUsers(pageable, search, status, role), "Success"));
    }

    @PutMapping("/{userId}/status")
    @Operation(summary = "Update User Status", description = "CA?p nhA?t trA!ng thA!i ngAEAE!i dA1ng (ACTIVE, INACTIVE, BANNED).")
    public ResponseEntity<ApiResponse<Void>> updateUserStatus(@PathVariable UUID userId, @RequestParam UserStatus status) {
        userCommandService.updateUserStatus(userId, status);
        return ResponseEntity.ok(ApiResponse.success(null, "User status updated successfully"));
    }

}