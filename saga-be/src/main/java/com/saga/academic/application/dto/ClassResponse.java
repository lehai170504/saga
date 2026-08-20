package com.saga.academic.application.dto;
import lombok.Builder;
import lombok.Data;
import java.util.UUID;
@Data
@Builder
public class ClassResponse {
    private UUID id;
    private String classCode;
}
