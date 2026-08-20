package com.saga.academic.application.dto;
import lombok.Builder;
import lombok.Data;
import java.util.UUID;

@Data
@Builder
public class CourseDTO {
    private UUID id;
    private UUID semesterId;
    private UUID subjectId;
    private UUID classId;
    private UUID instructorId;
    // We will leave these as basic UUIDs for now, unless we want to join user/subject tables
}
