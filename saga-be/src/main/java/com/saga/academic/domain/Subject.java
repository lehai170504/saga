package com.saga.academic.domain;
import lombok.Builder;
import lombok.Data;
import java.util.UUID;
@Data
@Builder
public class Subject {
    private UUID id;
    private String code;
    private String name;
}