package com.saga.auth.application.port;

import com.saga.auth.application.dto.UserProfileDTO;

public interface GoogleAuthPort {
    UserProfileDTO verifyToken(String idToken);
}
