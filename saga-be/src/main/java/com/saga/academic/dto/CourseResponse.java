package com.saga.academic.dto;

import lombok.Builder;
import lombok.Data;
import java.util.UUID;

@Data
@Builder
public class CourseResponse {
    private UUID id;
    private UUID semesterId;
    private String semesterName;
    private UUID subjectId;
    private String subjectName;
    private UUID classId;
    private String classCode;
    private UUID instructorId;
    private String instructorName;
}
