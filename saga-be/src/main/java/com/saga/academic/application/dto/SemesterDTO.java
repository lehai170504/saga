package com.saga.academic.application.dto;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class SemesterDTO {
    private UUID id;
    private String code;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean isActive;
}
