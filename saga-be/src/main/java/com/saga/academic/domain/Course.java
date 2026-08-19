package com.saga.academic.domain;
import lombok.Builder;
import lombok.Data;
import java.util.UUID;
@Data
@Builder
public class Course {
    private UUID id;
    private UUID semesterId;
    private UUID subjectId;
    private UUID classId;
    private UUID instructorId;
}