package com.saga.auth.application.port;

import com.saga.user.domain.User;

public interface JwtProviderPort {
    String generateToken(User user);
    boolean validateToken(String token);
    String getEmailFromToken(String token);
    String getRoleFromToken(String token);
}