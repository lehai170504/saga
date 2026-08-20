package com.saga.academic.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.UUID;

@Data
public class CourseRequest {
    @NotNull(message = "Semester ID is required")
    private UUID semesterId;

    @NotNull(message = "Subject ID is required")
    private UUID subjectId;

    @NotNull(message = "Class ID is required")
    private UUID classId;

    private UUID instructorId; // Có thể null nếu chưa gán giảng viên
}
