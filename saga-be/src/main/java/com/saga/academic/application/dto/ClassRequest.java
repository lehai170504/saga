package com.saga.academic.application.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
@Data
public class ClassRequest {
    @NotBlank(message = "Class code cannot be blank")
    private String classCode;
}
