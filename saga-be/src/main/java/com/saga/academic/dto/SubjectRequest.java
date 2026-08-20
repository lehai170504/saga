package com.saga.academic.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubjectRequest {
    @NotBlank(message = "Subject code cannot be blank")
    private String subjectCode;
    @NotBlank(message = "Subject name cannot be blank")
    private String subjectName;
}
