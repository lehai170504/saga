package com.saga.academic.application.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;
@Data
public class CreateSemesterRequest {
    @NotBlank(message = "Semester name cannot be blank")
    @NotBlank(message = "Semester code cannot be blank")
    private String code;
    @NotBlank(message = "Semester name cannot be blank")
    private String name;
    @NotNull(message = "End date cannot be null")
    private LocalDate startDate;
    @NotNull(message = "End date cannot be null")
    private LocalDate endDate;
}