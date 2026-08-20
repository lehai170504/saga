package com.saga.auth.service;

import com.saga.auth.dto.AuthResponse;
import com.saga.auth.dto.GoogleLoginRequest;
import com.saga.auth.dto.LocalLoginRequest;

public interface LoginUseCase {
    AuthResponse loginWithGoogle(GoogleLoginRequest request);

    AuthResponse loginLocal(LocalLoginRequest request);
}
