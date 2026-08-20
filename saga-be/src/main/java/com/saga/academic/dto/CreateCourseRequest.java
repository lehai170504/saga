package com.saga.academic.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCourseRequest {
    @NotNull(message = "Semester ID cannot be null")
    private UUID semesterId;
    @NotNull(message = "Subject ID cannot be null")
    private UUID subjectId;
    @NotNull(message = "Class ID cannot be null")
    private UUID classId;
    @NotNull(message = "Instructor ID cannot be null")
    private UUID instructorId;
}