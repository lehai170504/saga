package com.saga.academic.domain;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;
@Data
@Builder
public class Semester {
    private UUID id;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
}