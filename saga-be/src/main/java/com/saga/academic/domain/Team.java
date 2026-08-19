package com.saga.academic.domain;
import lombok.Builder;
import lombok.Data;
import java.util.UUID;
@Data
@Builder
public class Team {
    private UUID id;
    private UUID courseId;
    private String name;
}