package com.saga.application.port;

import com.saga.application.dto.UserProfileDTO;

public interface GoogleAuthPort {
    UserProfileDTO verifyToken(String idToken);
}
