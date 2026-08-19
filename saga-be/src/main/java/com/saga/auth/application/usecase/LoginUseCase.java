package com.saga.auth.application.usecase;

import com.saga.auth.application.dto.AuthResponse;
import com.saga.auth.application.dto.GoogleLoginRequest;

public interface LoginUseCase {
    AuthResponse loginWithGoogle(GoogleLoginRequest request);
}
