package com.saga.academic.application.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
@Data
public class SubjectRequest {
    @NotBlank(message = "Subject code cannot be blank")
    private String subjectCode;
    @NotBlank(message = "Subject name cannot be blank")
    private String subjectName;
}
