package com.saga.identity.application.port;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExternalUserProfile {
    private String id;
    private String name;
    private String email;
}
