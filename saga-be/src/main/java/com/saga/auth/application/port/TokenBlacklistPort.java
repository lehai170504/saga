package com.saga.auth.application.port;

public interface TokenBlacklistPort {
    void blacklistToken(String token);
    boolean isBlacklisted(String token);
}