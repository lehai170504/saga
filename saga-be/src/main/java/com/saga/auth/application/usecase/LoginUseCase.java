package com.saga.auth.application.usecase;

import com.saga.auth.application.dto.AuthResponse;
import com.saga.auth.application.dto.GoogleLoginRequest;
import com.saga.auth.application.dto.LocalLoginRequest;

public interface LoginUseCase {
    AuthResponse loginWithGoogle(GoogleLoginRequest request);

    AuthResponse loginLocal(LocalLoginRequest request);
}
