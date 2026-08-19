package com.saga.academic.domain;
import lombok.Builder;
import lombok.Data;
import java.util.UUID;
@Data
@Builder
public class ActiveSemesterSetting {
    private UUID id;
    private UUID semesterId;
}