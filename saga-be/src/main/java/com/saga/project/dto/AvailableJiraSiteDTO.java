package com.saga.project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailableJiraSiteDTO {
    private String id; // Cloud ID
    private String url;
    private String name;
}
