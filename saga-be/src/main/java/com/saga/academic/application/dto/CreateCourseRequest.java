package com.saga.academic.application.dto;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.UUID;
@Data
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