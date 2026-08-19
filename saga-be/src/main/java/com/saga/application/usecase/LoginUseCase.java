package com.saga.application.usecase;

import com.saga.application.dto.AuthResponse;
import com.saga.application.dto.GoogleLoginRequest;

public interface LoginUseCase {
    AuthResponse loginWithGoogle(GoogleLoginRequest request);
}
