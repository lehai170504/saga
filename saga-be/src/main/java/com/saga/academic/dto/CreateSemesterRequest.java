package com.saga.academic.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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