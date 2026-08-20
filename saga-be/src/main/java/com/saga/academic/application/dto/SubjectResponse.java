package com.saga.academic.application.dto;
import lombok.Builder;
import lombok.Data;
import java.util.UUID;
@Data
@Builder
public class SubjectResponse {
    private UUID id;
    private String subjectCode;
    private String subjectName;
}
