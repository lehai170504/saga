package com.saga.auth.entity;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import java.util.UUID;

@Data
@Builder
@RedisHash("RefreshToken")
public class RefreshToken {
    @Id
    private String token;
    private UUID userId;
    private String email;
    @TimeToLive
    private Long expiration; // In seconds
}